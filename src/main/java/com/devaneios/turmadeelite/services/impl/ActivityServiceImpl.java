package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.dto.ActivityCreateDTO;
import com.devaneios.turmadeelite.entities.Activity;
import com.devaneios.turmadeelite.entities.SchoolClass;
import com.devaneios.turmadeelite.entities.Teacher;
import com.devaneios.turmadeelite.repositories.ActivityRepository;
import com.devaneios.turmadeelite.repositories.SchoolClassRepository;
import com.devaneios.turmadeelite.repositories.TeacherRepository;
import com.devaneios.turmadeelite.services.ActivityService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sun.reflect.generics.repository.ClassRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final TeacherRepository teacherRepository;
    private final SchoolClassRepository classRepository;

    @Override
    @Transactional
    public void createActivity(ActivityCreateDTO activityCreateDTO,String teacherAuthUuid) {
        Teacher teacher = this.teacherRepository
                .findByAuthUuid(teacherAuthUuid)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.FORBIDDEN));
        Activity activity = Activity
                .builder()
                .name(activityCreateDTO.getName())
                .description(activityCreateDTO.getDescription())
                .punctuation(activityCreateDTO.getPunctuation())
                .isVisible(activityCreateDTO.getIsVisible())
                .isDeliverable(activityCreateDTO.getIsDeliverable())
                .maxDeliveryDate(activityCreateDTO.getMaxDeliveryDate())
                .teacher(teacher)
                .isActive(activityCreateDTO.getIsActive())
                .build();
        Activity savedActivity = this.activityRepository.save(activity);
        List<Long> schoolClassesIds = activityCreateDTO.getSchoolClasses();

        schoolClassesIds.forEach(schoolClassId -> {
            this.activityRepository.addActivityToClass(savedActivity.getId(), schoolClassId);
        });
    }

    @Override
    @Transactional
    public void updateActivity(ActivityCreateDTO activityCreateDTO,String teacherAuthUuid,Long activityId){
        Activity activity = this.activityRepository
                .findById(activityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Teacher teacher = this.teacherRepository
                .findByAuthUuid(teacherAuthUuid)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.FORBIDDEN));

        activity.setName(activityCreateDTO.getName());
        activity.setDescription(activityCreateDTO.getDescription());
        activity.setPunctuation(activity.getPunctuation());
        activity.setIsVisible(activityCreateDTO.getIsVisible());
        activity.setIsDeliverable(activityCreateDTO.getIsDeliverable());
        activity.setMaxDeliveryDate(activityCreateDTO.getMaxDeliveryDate());
        activity.setIsActive(activityCreateDTO.getIsActive());
        activity.setTeacher(teacher);

        List<SchoolClass> classes = activity.getClasses();

        classes
                .stream()
                .map(SchoolClass::getId)
                .forEach( classId -> {
                    this.activityRepository.removeActivityFromClass(activityId,classId);
                });

        List<Long> schoolClassesIds = activityCreateDTO.getSchoolClasses();
        schoolClassesIds.forEach(schoolClassId -> {
            this.activityRepository.addActivityToClass(activityId, schoolClassId);
        });
    }

    @Override
    public Page<Activity> getAllActivitiesOfTeacher(String teacherAuthUuid,int pageSize,int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        Teacher teacher = this.teacherRepository
                .findByAuthUuid(teacherAuthUuid)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.FORBIDDEN));
        return this.activityRepository.findAllByTeacherId(teacher.getId(), pageable).map(activity -> {
            List<SchoolClass> classesByActivityId = this.classRepository.findAllSchoolClassesByActivityId(activity.getId());
            activity.setClasses(classesByActivityId);
            return activity;
        });
    }

    @Override
    public Activity getActivityByIdAndTeacher(Long activityId, String teacherAuthUuid) {
        this.teacherRepository
                .findByAuthUuid(teacherAuthUuid)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.FORBIDDEN));
        return this.activityRepository.findById(activityId).map(activity -> {
            List<SchoolClass> classesByActivityId = this.classRepository.findAllSchoolClassesByActivityId(activityId);
            activity.setClasses(classesByActivityId);
            return activity;
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
