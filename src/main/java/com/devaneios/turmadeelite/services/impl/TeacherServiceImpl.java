package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.dto.ActivityPostDeliveryDTO;
import com.devaneios.turmadeelite.dto.StudentPunctuationDTO;
import com.devaneios.turmadeelite.entities.Role;
import com.devaneios.turmadeelite.entities.School;
import com.devaneios.turmadeelite.entities.Teacher;
import com.devaneios.turmadeelite.entities.UserCredentials;
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

import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@AllArgsConstructor
@Service
public class TeacherServiceImpl implements TeacherService {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final LogStatusUserRepository logStatusUserRepository;
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
}
