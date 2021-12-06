package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.dto.StudentRankingDTO;
import com.devaneios.turmadeelite.entities.*;
import com.devaneios.turmadeelite.repositories.ActivityDeliveryRepository;
import com.devaneios.turmadeelite.repositories.ActivityRepository;
import com.devaneios.turmadeelite.repositories.SchoolClassRepository;
import com.devaneios.turmadeelite.repositories.StudentRepository;
import com.devaneios.turmadeelite.services.RankingService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@AllArgsConstructor
@Service
public class RankingServiceImpl implements RankingService {

    private final SchoolClassRepository classRepository;
    private final StudentRepository studentRepository;
    private final ActivityRepository activityRepository;
    private final ActivityDeliveryRepository deliveryRepository;

    @Override
    public Page<SchoolClass> getRankeableClassesList(String studentAuthUuid, Integer size, Integer pageNumber) {
        Student student = this.studentRepository
                .findByAuthUuid(studentAuthUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));

        return this.classRepository.findAllByStudentIdAndIsDonePaginated(student.getId(), PageRequest.of(pageNumber,size));
    }

    @Override
    public List<StudentRankingDTO> getRankingByClass(String studentAuthUuid, Long classId) {
        Student student = this.studentRepository
                .findByAuthUuid(studentAuthUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));

        SchoolClass schoolClass = this.classRepository
                .findById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        TierConfig tierConfig = schoolClass.getTierConfig();

        List<Student> classStudents = this.studentRepository
                .findAllByClassId(schoolClass.getId());

        List<Activity> classActivities = this.activityRepository.findAllDoableActivitiesByClassId(classId);

        TreeSet<StudentRankingDTO> ranking = new TreeSet<>(Comparator.reverseOrder());

        for(Student classStudent: classStudents){
            int activitiesFound = 0;
            Double totalReceived = 0D;

            for(Activity classActivity: classActivities){
                Long studentId = classStudent.getId();
                Long activityId = classActivity.getId();

                Double gradeReceivedForActivity = this.deliveryRepository
                        .findStudentDeliveryForActivityWithAttachment(studentId, activityId)
                        .map(ActivityDelivery::getGradeReceived)
                        .filter(Objects::nonNull)
                        .filter(percentageReceived -> percentageReceived > 0)
                        .map( percentageReceived -> (percentageReceived / 100) * classActivity.getPunctuation())
                        .orElse(0D);

                totalReceived += gradeReceivedForActivity;
                activitiesFound +=1;
            }

            Double averageGrade = activitiesFound != 0 ? totalReceived / activitiesFound : 0;

            ranking.add(new StudentRankingDTO(classStudent.getId(),null,averageGrade,classStudent.getCredentials().getName(),null));
        }

        Iterator<StudentRankingDTO> rankingIterator = ranking.iterator();
        int position = 1;
        List<StudentRankingDTO> response = new ArrayList<>(4);
        while(rankingIterator.hasNext()){
            StudentRankingDTO next = rankingIterator.next();
            if(tierConfig!=null){
                if(position <= (ranking.size() * (tierConfig.getGoldPercent()/100))){
                    next.setTier(Tier.GOLD);
                } else if(position <= (ranking.size() * (tierConfig.getSilverPercent() / 100))){
                    next.setTier(Tier.SILVER);
                } else {
                    next.setTier(Tier.BRONZE);
                }
            }
            if(position<4){
                next.setPosition(position);
                response.add(next);
            }else{
                if(student.getId() == next.getStudentId()){
                    next.setPosition(position);
                    response.add(next);
                }
            }
            position++;
        }
        return response;
    }
}
