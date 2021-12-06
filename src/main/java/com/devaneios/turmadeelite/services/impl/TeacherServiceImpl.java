package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.dto.ActivityByTeacherDTO;
import com.devaneios.turmadeelite.dto.ActivityPostDeliveryDTO;
import com.devaneios.turmadeelite.dto.StudentPunctuationDTO;
import com.devaneios.turmadeelite.entities.*;
import com.devaneios.turmadeelite.events.UserCreated;
import com.devaneios.turmadeelite.exceptions.EmailAlreadyRegistered;
import com.devaneios.turmadeelite.repositories.*;
import com.devaneios.turmadeelite.services.SchoolService;
import com.devaneios.turmadeelite.services.TeacherService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class TeacherServiceImpl implements TeacherService {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final ActivityRepository activityRepository;
    private final StudentRepository studentRepository;
    private final LogStatusUserRepository logStatusUserRepository;
    private final ActivityDeliveryRepository activityDeliveryRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final SchoolService schoolService;

    @Transactional
    @Override
    public void createTeacherUser(String email, String name, String language, Boolean isActive, String managerAuthUuid) throws EmailAlreadyRegistered {
        if(this.userRepository.existsByEmail(email)){
            throw new EmailAlreadyRegistered();
        }

        School school = this.schoolService.findSchoolByManagerAuthUuid(managerAuthUuid);

        UserCredentials userCredentials = UserCredentials
                .builder()
                .email(email)
                .firstAccessToken(UUID.randomUUID().toString())
                .name(name)
                .isActive(isActive)
                .accessionDate(new Date())
                .role(Role.TEACHER)
                .build();

        UserCredentials userSaved = userRepository.save(userCredentials);
        this.teacherRepository.insertUserAsTeacher(userSaved.getId(),school.getId());
        eventPublisher.publishEvent(new UserCreated(this,userSaved,language));

        logStatusUserRepository.insertLogStatusUser(userCredentials.getId(), !userCredentials.getIsActive());
    }

    @Override
    public Page<Teacher> getPaginatedTeachers(int size, int pageNumber, String authUuid) {
        Pageable pageable = PageRequest.of(pageNumber, size);
        School school = this.schoolService.findSchoolByManagerAuthUuid(authUuid);
        return this.teacherRepository.findAllBySchoolId(school.getId(),pageable);
    }

    @Override
    public Teacher findTeacherById(Long id, String authUuid) {
        School school = this.schoolService.findSchoolByManagerAuthUuid(authUuid);
        return this.teacherRepository
                .findTeacherByIdWithSchoolAndCredentials(id,school.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }


    @Override
    public List<Teacher> findTeachersByEmailSubstring(String email, String managerAuthUuid) {
        School school = this.schoolService.findSchoolByManagerAuthUuid(managerAuthUuid);
        return this.teacherRepository.findTeacherByEmailLikeAndSchoolId("%" + email + "%",school.getId());
    }

    @Override
    public Optional<List<Teacher>> getTeachersByNameSimilarity(String name) {
        return this.teacherRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    @Transactional
    public void updateTeacherUser(
            String email,
            String name,
            String language,
            Boolean isActive,
            Long managerId,
            String managerAuthUuid) throws EmailAlreadyRegistered {
        Optional<UserCredentials> userCredentialsOptional = this.userRepository.findByEmail(email);

        UserCredentials userCredentials = null;

        if(userCredentialsOptional.isPresent()){
            userCredentials = userCredentialsOptional.get();
            if(!userCredentials.getEmail().equals(email))throw new EmailAlreadyRegistered();
        }else{
            userCredentials = this.userRepository
                    .findById(managerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        }

        School school = this.schoolService.findSchoolByManagerAuthUuid(managerAuthUuid);

        userCredentials.setEmail(email);
        userCredentials.setName(name);
        userCredentials.setIsActive(isActive);
        userRepository.save(userCredentials);

        Teacher teacher = this.teacherRepository.findById(managerId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        teacher.setSchool(school);

        this.teacherRepository.save(teacher);

        logStatusUserRepository.insertLogStatusUser(userCredentials.getId(), !userCredentials.getIsActive());
    }

    @Override
    public List<ActivityPostDeliveryDTO> getPostDeliveryActivities(String teacherAuthUuid) {
        List<ActivityPostDeliveryDTO> listActivityPostDelivery = new ArrayList<>();
        Optional<Teacher> teacherOptional = teacherRepository.findByAuthUuid(teacherAuthUuid);
        Teacher teacher = new Teacher();

        if(teacherOptional.isPresent())
            teacher = teacherOptional.get();

        List<Long> classesByTeacher = teacherRepository.getClassByTeacher(teacher.getId());

        if(classesByTeacher != null && classesByTeacher.size() > 0)
            for(int classTeacher = 0; classTeacher < classesByTeacher.stream().count(); classTeacher++) {
                ActivityPostDeliveryDTO a = new ActivityPostDeliveryDTO();

                int countActivity = schoolClassRepository.countActivityByClass(classesByTeacher.get(classTeacher));
                int studentsByClass = studentRepository.findAllByClassId(classesByTeacher.get(classTeacher)).size();
                int countDelivery = schoolClassRepository.countDeliveryByClass(classesByTeacher.get(classTeacher));
                String className = schoolClassRepository.findSchoolClassByClassId(classesByTeacher.get(classTeacher)).getName();

                a.setDeliveryActivity(countActivity * studentsByClass);
                a.setPostActivity(countDelivery);
                a.setClassName(className);

                listActivityPostDelivery.add(a);
            }

        return listActivityPostDelivery;
    }

    @Override
    public List<ActivityByTeacherDTO> getActivitiesByTeacher(String managerAuthUuid) {
        School school = this.schoolService.findSchoolByManagerAuthUuid(managerAuthUuid);
        List<Teacher> localTeachers = teacherRepository.findBySchool(school.getId());

        List<ActivityByTeacherDTO> listActivityByTeacher = new ArrayList<>();

        if (localTeachers != null && localTeachers.stream().count() > 0) {
            for (int i = 0; i < localTeachers.stream().count(); i++) {
                ActivityByTeacherDTO activityByTeacher = new ActivityByTeacherDTO();

                String teacherName = localTeachers.get(i).getCredentials().getName();

                Long teacherId = localTeachers.get(i).getId();
                int countActivityByTeacher = activityRepository.findAllByTeacherId(teacherId).size();

                activityByTeacher.setActivity(countActivityByTeacher);
                activityByTeacher.setTeacher(teacherName);

                listActivityByTeacher.add(activityByTeacher);
            }
        }

        listActivityByTeacher = listActivityByTeacher
                .stream()
                .sorted(Comparator.comparing(ActivityByTeacherDTO::getActivity))
                .collect(Collectors.toList());
        return listActivityByTeacher;
    }

    @Override
    public List<StudentPunctuationDTO> getStudentPunctuations(String teacherAuthUuid) {
        List<StudentPunctuationDTO> listStudentPunctuation = new ArrayList<>();
        Optional<Teacher> teacherOptional = teacherRepository.findByAuthUuid(teacherAuthUuid);
        Teacher teacher = new Teacher();

        if(teacherOptional.isPresent())
            teacher = teacherOptional.get();

        List<Long> classesByTeacher = teacherRepository.getClassByTeacher(teacher.getId());

        if(classesByTeacher != null && classesByTeacher.size() > 0)
            for(int classTeacher = 0; classTeacher < classesByTeacher.stream().count(); classTeacher++) {
                List<Student> students = studentRepository.findAllByClassId(classesByTeacher.get(classTeacher));

                if(students != null && students.stream().count() > 0)
                    for(int student = 0; student < students.stream().count(); student++) {
                        StudentPunctuationDTO s = new StudentPunctuationDTO();
                        String studentName = students.get(student).getCredentials().getName();
                        Double punctuation = activityDeliveryRepository.getPunctuationByStudent(students.get(student).getId(), teacher.getId());

                        s.setStudentName(studentName);
                        s.setPunctuation(punctuation);

                        listStudentPunctuation.add(s);

                    }
            }

        listStudentPunctuation = listStudentPunctuation
                .stream()
                .sorted(Comparator.comparing(StudentPunctuationDTO::getPunctuation))
                .collect(Collectors.toList());

        return listStudentPunctuation;
    }
}
