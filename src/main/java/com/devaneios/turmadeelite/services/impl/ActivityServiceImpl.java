package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.dto.ActivityCreateDTO;
import com.devaneios.turmadeelite.dto.AttachmentDTO;
import com.devaneios.turmadeelite.dto.StudentActivitiesDTO;
import com.devaneios.turmadeelite.dto.StudentActivityDetailsDTO;
import com.devaneios.turmadeelite.entities.*;
import com.devaneios.turmadeelite.repositories.*;
import com.devaneios.turmadeelite.services.ActivityDeliveryService;
import com.devaneios.turmadeelite.services.ActivityService;
import com.devaneios.turmadeelite.services.DataStorageService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

@Service
@AllArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final TeacherRepository teacherRepository;
    private final SchoolClassRepository classRepository;
    private final DataStorageService storageService;
    private final AttachmentRepository attachmentRepository;
    private final StudentRepository studentRepository;
    private final ActivityDeliveryService activityDeliveryService;

    @Override
    @Transactional
    public void createActivity(ActivityCreateDTO activityCreateDTO,String teacherAuthUuid) throws IOException, NoSuchAlgorithmException {
        Teacher teacher = this.teacherRepository
                .findByAuthUuid(teacherAuthUuid)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.FORBIDDEN));

        MultipartFile document = activityCreateDTO.getDocument();

        Attachment attachment = this.storageService.from(document, "activities/teachers-posts/");
        Attachment savedAttachment = this.attachmentRepository.save(attachment);
        Activity activity = Activity
                .builder()
                .name(activityCreateDTO.getName())
                .description(activityCreateDTO.getDescription())
                .punctuation(activityCreateDTO.getPunctuation())
                .isVisible(activityCreateDTO.getIsVisible())
                .isDeliverable(activityCreateDTO.getIsDeliverable())
                .maxDeliveryDate(activityCreateDTO.getFormattedDeliveryDate())
                .teacher(teacher)
                .isActive(activityCreateDTO.getIsActive())
                .attachment(savedAttachment)
                .build();

        Activity savedActivity = this.activityRepository.save(activity);
        List<Long> schoolClassesIds = activityCreateDTO.getSchoolClasses();

        schoolClassesIds.forEach(schoolClassId -> {
            this.activityRepository.addActivityToClass(savedActivity.getId(), schoolClassId);
        });

        this.storageService.uploadFile(attachment.getBucketKey(),(FileInputStream) document.getInputStream());
    }

    @Override
    @Transactional
    public void updateActivity(ActivityCreateDTO activityCreateDTO,String teacherAuthUuid,Long activityId) throws IOException, NoSuchAlgorithmException {
        Activity activity = this.activityRepository
                .findByIdWithAttachment(activityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Teacher teacher = this.teacherRepository
                .findByAuthUuid(teacherAuthUuid)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.FORBIDDEN));

        MultipartFile document = activityCreateDTO.getDocument();

        Attachment attachment = this.storageService.from(document, "activities/teachers-posts/");

        activity.setName(activityCreateDTO.getName());
        activity.setDescription(activityCreateDTO.getDescription());
        activity.setPunctuation(activity.getPunctuation());
        activity.setIsVisible(activityCreateDTO.getIsVisible());
        activity.setIsDeliverable(activityCreateDTO.getIsDeliverable());
        activity.setMaxDeliveryDate(activityCreateDTO.getFormattedDeliveryDate());
        activity.setIsActive(activityCreateDTO.getIsActive());
        activity.setTeacher(teacher);

        List<SchoolClass> classes = activity.getClasses();

        classes
                .stream()
                .map(SchoolClass::getId)
                .forEach( classId -> {
                    this.activityRepository.removeActivityFromClass(activityId,classId);
                });

        List<Long> schoolClassesIds = activityCreateDTO.getSchoolClasses();
        schoolClassesIds.forEach(schoolClassId -> {
            this.activityRepository.addActivityToClass(activityId, schoolClassId);
        });

        String newMd5 = attachment.getFileMd5();
        Attachment oldAttachment = activity.getAttachment();
        String oldMd5 = oldAttachment.getFileMd5();

        if(!oldMd5.equals(newMd5)){
            activity.setAttachment(attachment);
            this.attachmentRepository.save(attachment);
            this.storageService.deleteObject(oldAttachment.getBucketKey());
            this.storageService.uploadFile(attachment.getBucketKey(),(FileInputStream) document.getInputStream());
        }
        this.activityRepository.save(activity);
    }

    @Override
    public Page<Activity> getAllActivitiesOfTeacher(String teacherAuthUuid,int pageSize,int pageNumber){
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        Teacher teacher = this.teacherRepository
                .findByAuthUuid(teacherAuthUuid)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.FORBIDDEN));
        return this.activityRepository.findAllByTeacherId(teacher.getId(), pageable).map(activity -> {
            List<SchoolClass> classesByActivityId = this.classRepository.findAllSchoolClassesByActivityId(activity.getId());
            activity.setClasses(classesByActivityId);
            return activity;
        });
    }

    @Override
    public Activity getActivityByIdAndTeacher(Long activityId, String teacherAuthUuid) {
        this.teacherRepository
                .findByAuthUuid(teacherAuthUuid)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.FORBIDDEN));
        return this.activityRepository.findByIdWithAttachment(activityId).map(activity -> {
            List<SchoolClass> classesByActivityId = this.classRepository.findAllSchoolClassesByActivityId(activityId);
            activity.setClasses(classesByActivityId);
            return activity;
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public AttachmentDTO getTeacherAttachmentFromActivity(Long id, String teacherAuthUuid) throws IOException {
        this.teacherRepository
                .findByAuthUuid(teacherAuthUuid)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.FORBIDDEN));

        Activity activity = this.activityRepository
                .findByIdWithAttachment(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Attachment attachment = activity.getAttachment();

        if(attachment==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        InputStream inputStream = this.storageService.downloadFile(attachment.getBucketKey());
        return new AttachmentDTO(attachment.getFilename(),inputStream);
    }

    @Override
    public List<StudentActivitiesDTO> getStudentActivities(String studentAuthUuid) {
        Student student = this.studentRepository
                .findByAuthUuid(studentAuthUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));

        List<StudentActivitiesDTO> activitiesResponse = new LinkedList<>();

        List<SchoolClass> allClassesByStudent = this.classRepository.findAllByStudentId(student.getId());
        for(SchoolClass schoolClass:allClassesByStudent){
            List<Activity> activitiesByClass = this.activityRepository.findAllDoableActivitiesByClassId(schoolClass.getId());
            for(Activity activity:activitiesByClass){
                Teacher teacher = this.activityRepository.findTeacherByActivity(activity.getId());
                activity.setTeacher(teacher);
                ActivityStatus activityStatus = this.activityDeliveryService.getStatusFrom(activity, student);
                activitiesResponse.add(new StudentActivitiesDTO(activity,schoolClass.getId(),activityStatus));
            }
        }

        return activitiesResponse;
    }

    @Override
    public StudentActivityDetailsDTO getActivityDetailsById(String studentAuthUuid, Long activityId, Long classId) {
        Student student = this.studentRepository
                .findByAuthUuid(studentAuthUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));

        SchoolClass schoolClass = this.classRepository
                .findById(classId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return this.activityRepository
                .findByClassIdAndActivityId(classId,activityId)
                .map( activity -> {
                    Teacher teacher = this.activityRepository.findTeacherByActivity(activity.getId());
                    activity.setTeacher(teacher);
                    Attachment attachment = this.attachmentRepository.findByActivityId(activity.getId());
                    activity.setAttachment(attachment);
                    String filename = this.activityDeliveryService.deliveryFilename(student, activity);
                    return new StudentActivityDetailsDTO(activity,schoolClass,filename);
                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public AttachmentDTO getStudentAttachmentFromActivity(Long id, String studentAuthUuid) throws IOException {
        this.studentRepository
                .findByAuthUuid(studentAuthUuid)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.FORBIDDEN));

        Activity activity = this.activityRepository
                .findByIdWithAttachment(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Attachment attachment = activity.getAttachment();

        if(attachment==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        InputStream inputStream = this.storageService.downloadFile(attachment.getBucketKey());
        return new AttachmentDTO(attachment.getFilename(),inputStream);
    }
}
