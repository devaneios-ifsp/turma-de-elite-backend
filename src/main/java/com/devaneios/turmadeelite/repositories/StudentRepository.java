package com.devaneios.turmadeelite.repositories;

import com.devaneios.turmadeelite.entities.Student;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StudentRepository extends CrudRepository<Student,Long> {

    @Query("FROM Student s")
    List<Student> findByClassId(Long classId);
}
