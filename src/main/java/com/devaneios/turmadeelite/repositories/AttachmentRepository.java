package com.devaneios.turmadeelite.repositories;

import com.devaneios.turmadeelite.entities.Attachment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface AttachmentRepository extends CrudRepository<Attachment,Long> {
    @Query(value = "SELECT a.* FROM activity ac INNER JOIN attachments a ON ac.attachment_id=a.id WHERE ac.id=:activityId",nativeQuery = true)
    Attachment findByActivityId(Long activityId);
}
