package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.dto.AttachmentDTO;
import com.devaneios.turmadeelite.entities.*;
import com.devaneios.turmadeelite.repositories.ActivityDeliveryRepository;
import com.devaneios.turmadeelite.repositories.ActivityRepository;
import com.devaneios.turmadeelite.repositories.AttachmentRepository;
import com.devaneios.turmadeelite.repositories.StudentRepository;
import com.devaneios.turmadeelite.services.ActivityDeliveryService;
import com.devaneios.turmadeelite.services.DataStorageService;
import lombok.AllArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ActivityDeliveryServiceImpl implements ActivityDeliveryService {

    private final DataStorageService storageService;
    private final AttachmentRepository attachmentRepository;
    private final ActivityRepository activityRepository;
    private final StudentRepository studentRepository;
    private final ActivityDeliveryRepository deliveryRepository;

    @Transactional
    @Override
    public void deliveryActivity(MultipartFile deliveryDocument,Long activityId, String studentAuthUuid) throws IOException, NoSuchAlgorithmException {

        Student student = this.studentRepository
                .findByAuthUuid(studentAuthUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Activity activity = this.activityRepository
                .findById(activityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ActivityStatus status = this.getStatusFrom(activity, student);

        if(status!=ActivityStatus.NON_DELIVERED){
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED);
        }

        Attachment attachment = this.storageService.from(deliveryDocument, "activities/student-deliveries/" + activityId + "/");
        Attachment savedAttachment = this.attachmentRepository.save(attachment);

        ActivityDelivery activityDelivery = ActivityDelivery
                .builder()
                .deliveryTimestamp(LocalDateTime.now(ZoneId.of("GMT-3")))
                .student(student)
                .activity(activity)
                .attachment(savedAttachment)
                .build();

        this.deliveryRepository.save(activityDelivery);
        this.storageService.uploadFile(attachment.getBucketKey(),(FileInputStream) deliveryDocument.getInputStream());
    }

    @Override
    public ActivityStatus getActivityStatus(Long activityId, String studentAuthUuid) {
        Activity activity = this.activityRepository
                .findById(activityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Student student = this.studentRepository
                .findByAuthUuid(studentAuthUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return this.getStatusFrom(activity,student);

    }

    @Override
    public ActivityStatus getStatusFrom(Activity activity,Student student){
        ActivityDelivery delivery = this.deliveryRepository
                .findStudentDeliveryForActivity(student.getId(), activity.getId())
                .orElse(null);

        if(Objects.isNull(delivery)){
            LocalDateTime maxDeliveryDate = activity.getMaxDeliveryDate();
            LocalDateTime now = LocalDateTime.now(ZoneId.of("GMT-3"));
            if(now.isAfter(maxDeliveryDate)){
                return ActivityStatus.EXPIRED;
            }else{
                return ActivityStatus.NON_DELIVERED;
            }
        }else{
            Float gradeReceived = delivery.getGradeReceived();
            if(Objects.nonNull(gradeReceived)){
                return ActivityStatus.REVISED;
            }else{
                return ActivityStatus.DELIVERED;
            }
        }
    }

    @Override
    public String deliveryFilename(Student student, Activity activity) {
        return this.deliveryRepository
                .findStudentDeliveryForActivityWithAttachment(student.getId(), activity.getId())
                .map(ActivityDelivery::getAttachment)
                .map(Attachment::getFilename)
                .orElse(null);
    }

    @Override
    public AttachmentDTO getStudentDeliveryAttachment(Long activityId, String studentAuthUuid) throws IOException {
        Student student = this.studentRepository
                .findByAuthUuid(studentAuthUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));

        ActivityDelivery delivery = this.deliveryRepository
                .findStudentDeliveryForActivityWithAttachment(student.getId(), activityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Attachment attachment = delivery.getAttachment();

        if(attachment==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        InputStream inputStream = this.storageService.downloadFile(attachment.getBucketKey());
        return new AttachmentDTO(attachment.getFilename(),inputStream);
    }
}
