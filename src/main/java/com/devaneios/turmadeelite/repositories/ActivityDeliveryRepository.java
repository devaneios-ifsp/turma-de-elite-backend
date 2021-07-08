package com.devaneios.turmadeelite.repositories;

import com.devaneios.turmadeelite.entities.ActivityDelivery;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityDeliveryRepository extends CrudRepository<ActivityDelivery,Long> {
    List<ActivityDelivery> findAll();

    List<ActivityDelivery> findByStudentAndClass(Long studentId, Long classId);
}
