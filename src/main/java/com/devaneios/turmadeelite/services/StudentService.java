package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.dto.StudentCreateDTO;
import com.devaneios.turmadeelite.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    void createStudent(StudentCreateDTO studentDTO, String managerAuthUuid);

    Page<Student> getPaginatedStudents(int size, int pageNumber, String managerAuthUuid);

    void updateStudent(StudentCreateDTO studentCreateDTO, Long id, String managerAuthUuid);

    Student findStudentById(Long id, String managerAuthUuid);

    List<Student> findByStudentRegistrySimilarity(String registry, String principal);
}
