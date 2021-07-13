package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.entities.Teacher;
import com.devaneios.turmadeelite.exceptions.EmailAlreadyRegistered;
import org.springframework.data.domain.Page;

public interface TeacherService {
    void createTeacherUser(String email, String name, String language, Boolean isActive, String managerAuthUuid) throws EmailAlreadyRegistered;

    Page<Teacher> getPaginatedTeachers(int size, int pageNumber, String authUuid);

    Teacher findTeacherById(Long id, String authUuid);

    void updateTeacherUser(String email, String name, String language, Boolean isActive,Long teacherId, String managerAuthUuid) throws EmailAlreadyRegistered;
}
