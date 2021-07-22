package com.devaneios.turmadeelite.repositories;

import com.devaneios.turmadeelite.entities.ActivityDelivery;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityDeliveryRepository extends CrudRepository<ActivityDelivery,Long> {

    @Query("SELECT ad FROM ActivityDelivery ad JOIN ad.student s JOIN ad.activity a WHERE a.id=:activityId AND s.id=:studentId")
    Optional<ActivityDelivery> findStudentDeliveryForActivity(Long studentId, Long activityId);

    @Query("SELECT ad FROM ActivityDelivery ad JOIN FETCH ad.attachment a JOIN ad.student s JOIN s.credentials c JOIN ad.activity a WHERE a.id=:activityId AND s.id=:studentId")
    Optional<ActivityDelivery> findStudentDeliveryForActivityWithAttachment(Long studentId, Long activityId);

    @Query("SELECT ad FROM ActivityDelivery ad JOIN FETCH ad.attachment a WHERE ad.id=:deliveryId")
    Optional<ActivityDelivery> findByIdWIthAttachment(Long deliveryId);

    @Query("SELECT ad FROM ActivityDelivery ad JOIN FETCH ad.attachment a JOIN ad.student s JOIN s.credentials c JOIN ad.activity a WHERE a.id=:activityId")
    List<ActivityDelivery> findStudentDeliveriesForActivityWithAttachment(Long activityId);

    @Modifying
    @Query(value = "UPDATE activity_delivery SET grade_received=:gradePercentage WHERE id=:deliveryId ;",nativeQuery = true)
    void giveGradeToDeliveryId(Long deliveryId, Float gradePercentage);

//    List<ActivityDelivery> findAll();
//
//    @Query("FROM ActivityDelivery a")
//    List<ActivityDelivery> findByStudentAndClass(Long studentId, Long classId);
}
