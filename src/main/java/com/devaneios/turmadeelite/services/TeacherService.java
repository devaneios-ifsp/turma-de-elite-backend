package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.entities.Teacher;
import com.devaneios.turmadeelite.exceptions.EmailAlreadyRegistered;
import org.springframework.data.domain.Page;

public interface TeacherService {
    void createTeacherUser(String email, String name, String language, Long schoolId, Boolean isActive) throws EmailAlreadyRegistered;

    Page<Teacher> getPaginatedTeachers(int size, int pageNumber);

    Teacher findTeacherById(Long id);

    void updateTeacherUser(String email, String name, String language, Long schoolId, Boolean isActive,Long managerId) throws EmailAlreadyRegistered;
}
