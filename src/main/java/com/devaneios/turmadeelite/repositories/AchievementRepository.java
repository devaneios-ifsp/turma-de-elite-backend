package com.devaneios.turmadeelite.repositories;

import com.devaneios.turmadeelite.entities.Achievement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AchievementRepository extends CrudRepository<Achievement, Long> {
    List<Achievement> findAll();

    @Query("SELECT a FROM Achievement a JOIN a.teacher t WHERE t.id=:teacherId")
    Page<Achievement> findByTeacherId(Long teacherId, Pageable page);

    @Query("SELECT a FROM Achievement a JOIN FETCH a.students s WHERE s.id=:studentId ")
    Set<Achievement> findAllAcquiredByStudentId(Long studentId);

    @Query("SELECT a FROM Achievement a JOIN a.schoolClass c WHERE c.id=:classId")
    List<Achievement> findAllByClassId(Long classId);

    @Query("SELECT a FROM Achievement a JOIN a.activity ac WHERE ac.id=:activityId")
    List<Achievement> fidAllByActivityId(Long activityId);
}
