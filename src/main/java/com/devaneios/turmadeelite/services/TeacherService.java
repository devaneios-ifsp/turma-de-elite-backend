package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.dto.ActivityPostDeliveryDTO;
import com.devaneios.turmadeelite.dto.StudentPunctuationDTO;
import com.devaneios.turmadeelite.entities.Teacher;
import com.devaneios.turmadeelite.exceptions.EmailAlreadyRegistered;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface TeacherService {
    void createTeacherUser(String email, String name, String language, Boolean isActive, String managerAuthUuid) throws EmailAlreadyRegistered;

    Page<Teacher> getPaginatedTeachers(int size, int pageNumber, String authUuid);

    Teacher findTeacherById(Long id, String authUuid);

    void updateTeacherUser(String email, String name, String language, Boolean isActive,Long teacherId, String managerAuthUuid) throws EmailAlreadyRegistered;

    List<Teacher> findTeachersByEmailSubstring(String email, String managerAuthUuid);

    Optional<List<Teacher>> getTeachersByNameSimilarity(String name);

    List<ActivityPostDeliveryDTO> getPostDeliveryActivities(String teacherAuthUuid);

    List<StudentPunctuationDTO> getStudentPunctuations(String teacherAuthUuid);
}
