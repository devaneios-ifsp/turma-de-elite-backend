package com.devaneios.turmadeelite.repositories;

import com.devaneios.turmadeelite.entities.ExternalStudentGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExternalStudentGradeRepository extends JpaRepository<ExternalStudentGrade, Long> {
    List<ExternalStudentGrade> findByExternalClassId(String classId);
}