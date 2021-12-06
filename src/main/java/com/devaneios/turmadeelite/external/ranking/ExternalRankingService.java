package com.devaneios.turmadeelite.external.ranking;

import com.devaneios.turmadeelite.dto.SchoolClassViewDTO;
import com.devaneios.turmadeelite.dto.StudentRankingDTO;
import com.devaneios.turmadeelite.entities.ExternalClassConfig;
import com.devaneios.turmadeelite.entities.ExternalStudentGrade;
import com.devaneios.turmadeelite.entities.Student;
import com.devaneios.turmadeelite.entities.Tier;
import com.devaneios.turmadeelite.external.courses.ExternalCoursesService;
import com.devaneios.turmadeelite.repositories.ExternalClassConfigRepository;
import com.devaneios.turmadeelite.repositories.ExternalStudentGradeRepository;
import com.devaneios.turmadeelite.repositories.StudentRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ExternalRankingService {

    private ExternalCoursesService coursesService;
    private final ExternalClassConfigRepository classConfigRepository;
    private final ExternalStudentGradeRepository studentGradeRepository;
    private final StudentRepository studentRepository;

    public List<SchoolClassViewDTO> getRankeableClasses(String authUuid) throws IOException {
        return this.coursesService
                .getAllCourses(authUuid)
                .stream()
                .filter(
                        schoolClassViewDTO -> this.classConfigRepository
                        .findByExternalClassId(schoolClassViewDTO.getExternalId())
                        .map(ExternalClassConfig::getIsClosed)
                        .orElse(false)
                )
                .collect(Collectors.toList());
    }

    public List<StudentRankingDTO> getRankingByClass(String authUuid, String classId) {
        ExternalClassConfig externalClassConfig = this.classConfigRepository
                .findByExternalClassId(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.PRECONDITION_REQUIRED));

        Student student = this.studentRepository
                .findByAuthUuid(authUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));

        List<ExternalStudentGrade> studentGrades = this.studentGradeRepository.findByExternalClassId(classId);
        studentGrades.sort(Comparator.comparing(ExternalStudentGrade::getGrade,Comparator.reverseOrder()));
        int position = 1;
        List<StudentRankingDTO> response = new ArrayList<>(4);
        for(ExternalStudentGrade studentGrade:studentGrades){
            StudentRankingDTO rankingDTO = new StudentRankingDTO();
            if(position <= (studentGrades.size() * (externalClassConfig.getGoldPercent()/100))){
                rankingDTO.setTier(Tier.GOLD);
            } else if(position <= (studentGrades.size() * (externalClassConfig.getSilverPercent() / 100))){
                rankingDTO.setTier(Tier.SILVER);
            } else {
                rankingDTO.setTier(Tier.BRONZE);
            }

            if(position<4){
                rankingDTO.setPosition(position);
                rankingDTO.setGrade(studentGrade.getGrade());
                rankingDTO.setName(studentGrade.getStudent().getName());
                response.add(rankingDTO);
            }else{
                if(student.getId().equals(studentGrade.getStudent().getId())){
                    rankingDTO.setPosition(position);
                    rankingDTO.setGrade(studentGrade.getGrade());
                    rankingDTO.setName(studentGrade.getStudent().getName());
                    response.add(rankingDTO);
                    break;
                }
            }
            position++;
        }
        return response;
    }
}
