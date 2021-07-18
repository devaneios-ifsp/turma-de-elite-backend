package com.devaneios.turmadeelite.repositories;

import com.devaneios.turmadeelite.entities.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends CrudRepository<Activity,Long> {

    @Modifying
    @Query(value = "INSERT INTO class_activities (class_id,activity_id) VALUES (:classId,:activityId) ;",nativeQuery = true)
    void addActivityToClass(Long activityId,Long classId);

    @Modifying
    @Query(value = "DELETE class_activities WHERE class_id=:classId AND activity_id=:activityId ;",nativeQuery = true)
    void removeActivityFromClass(Long activityId,Long classId);

    @Query("SELECT a FROM Activity a JOIN a.teacher t WHERE t.id=:teacherId")
    Page<Activity> findAllByTeacherId(Long teacherId, Pageable pageable);
}
