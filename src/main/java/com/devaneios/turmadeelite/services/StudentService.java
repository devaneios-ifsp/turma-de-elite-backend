package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.dto.StudentCreateDTO;
import com.devaneios.turmadeelite.entities.Student;
import com.devaneios.turmadeelite.exceptions.EmailAlreadyRegistered;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StudentService {
    void createStudent(StudentCreateDTO studentDTO, String managerAuthUuid);

    Page<Student> getPaginatedStudents(int size, int pageNumber, String managerAuthUuid);

    void updateStudent(StudentCreateDTO studentCreateDTO, Long id, String managerAuthUuid) throws EmailAlreadyRegistered;

    Student findStudentById(Long id, String managerAuthUuid);

    List<Student> findByStudentRegistrySimilarity(String registry, String principal);
}
