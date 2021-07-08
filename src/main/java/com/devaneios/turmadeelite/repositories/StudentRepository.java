package com.devaneios.turmadeelite.repositories;

import com.devaneios.turmadeelite.entities.Student;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StudentRepository extends CrudRepository<Student,Long> {
    List<Student> findByClassId(Long classId);
}
