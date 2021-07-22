package com.devaneios.turmadeelite.repositories;

import com.devaneios.turmadeelite.entities.Achievement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRepository extends CrudRepository<Achievement, Long> {
    List<Achievement> findAll();

    @Query("SELECT a FROM Achievement a JOIN a.teacher t WHERE t.id=:teacherId")
    Page<Achievement> findByTeacherId(Long teacherId, Pageable page);
}
