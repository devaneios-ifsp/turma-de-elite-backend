package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.dto.AchievementCreateDTO;
import com.devaneios.turmadeelite.dto.AchievementPanelDTO;
import com.devaneios.turmadeelite.entities.*;
import com.devaneios.turmadeelite.repositories.*;
import com.devaneios.turmadeelite.services.AchievementService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AchievementServiceImpl implements AchievementService {

    private final AchievementRepository achievementRepository;
    private final ActivityRepository activityRepository;
    private final SchoolClassRepository classRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    @Override
    public void createAchievement(AchievementCreateDTO achievementCreateDTO, String teacherAuthUuid) {
        Teacher teacher = this.teacherRepository
                .findByAuthUuid(teacherAuthUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
        Achievement achievement = achievementCreateDTO.toEntity(teacher);
        this.achievementRepository.save(achievement);
    }

    @Override
    public Page<Achievement> getAchievements(int size, int pageNumber, String teacherAuthUuid) {
        Teacher teacher = this.teacherRepository
                .findByAuthUuid(teacherAuthUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));

        Pageable page = PageRequest.of(pageNumber,size);
        return this.achievementRepository.findByTeacherId(teacher.getId(),page);
    }

    @Override
    public Optional<Achievement> findAchievementById(Long achievementId) {
        return this.achievementRepository.findById(achievementId);
    }

    @Override
    public void updateAchievement(Long achievementId, AchievementCreateDTO achievementCreateDTO, String teacherAuthUuid) {
        this.achievementRepository.findById(achievementId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Teacher teacher = this.teacherRepository
                .findByAuthUuid(teacherAuthUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
        Achievement achievement = achievementCreateDTO.toEntity(teacher);
        achievement.setId(achievementId);
        this.achievementRepository.save(achievement);
    }

    @Override
    public List<AchievementPanelDTO> getStudentAchievements(String studentAuthUuid) {
        Student student = this.studentRepository
                .findByAuthUuid(studentAuthUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
//        List<SchoolClass> studentClasses = this.classRepository.findAllNotIsDoneByStudentId(student.getId());
//        studentClasses.addAll(this.classRepository.findAllByStudentIdAndIsDone(student.getId()));
//        List<Achievement> allAchievements = new LinkedList<>();
//        for(SchoolClass schoolClass: studentClasses){
//            List<Achievement> achievementsByClass = this.achievementRepository.findAllByClassId(schoolClass.getId());
//            allAchievements.addAll(achievementsByClass);
//            List<Activity> activities = this.activityRepository.findAllDoableActivitiesByClassId(schoolClass.getId());
//            for(Activity activity: activities){
//                List<Achievement> achievements = this.achievementRepository.fidAllByActivityId(activity.getId());
//                allAchievements.addAll(achievements);
//            }
//        }
        List<Achievement> allAchievements = this.achievementRepository.findAll();
        Set<Achievement> allAcquiredByStudentId = this.achievementRepository.findAllAcquiredByStudentId(student.getId());

        return allAchievements
                .stream()
                .map( achievement ->  new AchievementPanelDTO(achievement,allAcquiredByStudentId.contains(achievement)))
                .collect(Collectors.toList());
    }
}
