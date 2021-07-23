package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.dto.ActivityCreateDTO;
import com.devaneios.turmadeelite.dto.AttachmentDTO;
import com.devaneios.turmadeelite.dto.StudentActivitiesDTO;
import com.devaneios.turmadeelite.dto.StudentActivityDetailsDTO;
import com.devaneios.turmadeelite.entities.Activity;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface ActivityService {
    void createActivity(ActivityCreateDTO activityCreateDTO, MultipartFile document, String teacherAuthUuid) throws IOException, NoSuchAlgorithmException;
    void updateActivity(ActivityCreateDTO activityCreateDTO, MultipartFile document, String teacherAuthUuid, Long activityId) throws IOException, NoSuchAlgorithmException;
    Page<Activity> getAllActivitiesOfTeacherPaginated(String teacherAuthUuid, int pageSize, int pageNumber);
    Activity getActivityByIdAndTeacher(Long activityId, String teacherAuthUuid);
    AttachmentDTO getTeacherAttachmentFromActivity(Long id, String teacherAuthUuid) throws IOException;
    List<StudentActivitiesDTO> getStudentActivities(String studentAuthUuid);

    StudentActivityDetailsDTO getActivityDetailsById(String studentAuthUuid, Long activityId, Long classId);

    AttachmentDTO getStudentAttachmentFromActivity(Long id, String studentAuthUuid) throws IOException;

    List<Activity> getAllActivitiesOfTeacher(String teacherAuthUUid);
}
