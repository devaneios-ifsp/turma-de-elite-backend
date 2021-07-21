package com.devaneios.turmadeelite.repositories;

import com.devaneios.turmadeelite.entities.Activity;
import com.devaneios.turmadeelite.entities.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.DoubleStream;

@Repository
public interface ActivityRepository extends CrudRepository<Activity,Long> {

    @Modifying
    @Query(value = "INSERT INTO class_activities (class_id,activity_id) VALUES (:classId,:activityId) ;",nativeQuery = true)
    void addActivityToClass(Long activityId,Long classId);

    @Modifying
    @Query(value = "DELETE FROM class_activities WHERE class_id=:classId AND activity_id=:activityId ;",nativeQuery = true)
    void removeActivityFromClass(Long activityId,Long classId);

    @Query("SELECT a FROM Activity a JOIN a.teacher t WHERE t.id=:teacherId")
    Page<Activity> findAllByTeacherId(Long teacherId, Pageable pageable);

    @Query("SELECT a FROM Activity a LEFT JOIN FETCH a.attachment t WHERE a.id=:activityId")
    Optional<Activity> findByIdWithAttachment(Long activityId);

    @Query("SELECT DISTINCT a FROM Activity a " +
            "JOIN a.classes c " +
            "JOIN c.studentsMemberships stm " +
            "JOIN stm.student st " +
            "WHERE st.id=:id AND a.isVisible=true AND a.isActive=true AND a.isDeliverable=true")
    Page<Activity> findAllByStudentId(Long id, Pageable page);

    @Query("SELECT t FROM Teacher t JOIN t.activities at WHERE at.id=:id")
    Teacher findTeacherByActivity(Long id);

    @Query("SELECT a FROM Activity a " +
            "JOIN a.classes c " +
            "WHERE c.id=:classId " +
            "AND a.isVisible=true AND a.isActive=true AND a.isDeliverable=true AND a.id=:activityId")
    Optional<Activity> findByClassIdAndActivityId(Long classId, Long activityId);

    @Query("SELECT a FROM Activity a JOIN a.classes c WHERE c.id=:classId AND a.isVisible=true AND a.isActive=true AND a.isDeliverable=true")
    List<Activity> findAllDoableActivitiesByClassId(Long classId);
}
