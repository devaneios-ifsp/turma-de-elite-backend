package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.dto.ActivityCreateDTO;
import com.devaneios.turmadeelite.entities.Activity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ActivityService {
    void createActivity(ActivityCreateDTO activityCreateDTO,String teacherAuthUuid);
    void updateActivity(ActivityCreateDTO activityCreateDTO,String teacherAuthUuid,Long activityId);
    Page<Activity> getAllActivitiesOfTeacher(String teacherAuthUuid,int pageSize,int pageNumber);
}
