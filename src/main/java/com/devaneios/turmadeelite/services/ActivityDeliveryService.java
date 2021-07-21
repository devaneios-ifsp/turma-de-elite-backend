package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.dto.AttachmentDTO;
import com.devaneios.turmadeelite.entities.Activity;
import com.devaneios.turmadeelite.entities.ActivityStatus;
import com.devaneios.turmadeelite.entities.Student;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface ActivityDeliveryService {
    void deliveryActivity(MultipartFile deliveryDocument,Long activityId, String studentAuthUuid) throws IOException, NoSuchAlgorithmException;
    ActivityStatus getActivityStatus(Long activityId, String studentAuthUuid);
    ActivityStatus getStatusFrom(Activity activity, Student student);
    String deliveryFilename(Student student, Activity activity);

    AttachmentDTO getStudentDeliveryAttachment(Long activityId, String studentAuthUuid) throws IOException;
}
