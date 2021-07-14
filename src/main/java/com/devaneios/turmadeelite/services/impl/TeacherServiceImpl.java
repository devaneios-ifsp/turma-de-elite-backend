package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.entities.*;
import com.devaneios.turmadeelite.events.UserCreated;
import com.devaneios.turmadeelite.exceptions.EmailAlreadyRegistered;
import com.devaneios.turmadeelite.repositories.ManagerRepository;
import com.devaneios.turmadeelite.repositories.SchoolRepository;
import com.devaneios.turmadeelite.repositories.TeacherRepository;
import com.devaneios.turmadeelite.repositories.UserRepository;
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

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class TeacherServiceImpl implements TeacherService {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
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
                .role(Role.TEACHER)
                .build();

        UserCredentials userSaved = userRepository.save(userCredentials);
        this.teacherRepository.insertUserAsTeacher(userSaved.getId(),school.getId());
        eventPublisher.publishEvent(new UserCreated(this,userSaved,language));
    }

    @Override
    public Page<Teacher> getPaginatedTeachers(int size, int pageNumber, String authUuid) {
        Pageable pageable = PageRequest.of(pageNumber, size);
        School school = this.schoolService.findSchoolByManagerAuthUuid(authUuid);;
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
    }

}
