package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.dto.StudentCreateDTO;
import com.devaneios.turmadeelite.entities.Student;
import org.springframework.data.domain.Page;

public interface StudentService {
    void createStudent(StudentCreateDTO studentDTO, String managerAuthUuid);

    Page<Student> getPaginatedStudents(int size, int pageNumber, String managerAuthUuid);

    void updateStudent(StudentCreateDTO studentCreateDTO, Long id, String managerAuthUuid);

    Student findStudentById(Long id, String managerAuthUuid);
}
