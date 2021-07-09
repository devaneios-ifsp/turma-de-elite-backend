package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.entities.*;
import com.devaneios.turmadeelite.events.UserCreated;
import com.devaneios.turmadeelite.exceptions.EmailAlreadyRegistered;
import com.devaneios.turmadeelite.repositories.SchoolRepository;
import com.devaneios.turmadeelite.repositories.TeacherRepository;
import com.devaneios.turmadeelite.repositories.UserRepository;
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
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class TeacherServiceImpl implements TeacherService {

    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final TeacherRepository teacherRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public void createTeacherUser(String email, String name, String language, Long schoolId, Boolean isActive) throws EmailAlreadyRegistered {
        if(this.userRepository.existsByEmail(email)){
            throw new EmailAlreadyRegistered();
        }

        School school = this.findSchoolById(schoolId);

        UserCredentials userCredentials = UserCredentials
                .builder()
                .email(email)
                .firstAccessToken(UUID.randomUUID().toString())
                .name(name)
                .isActive(isActive)
                .role(Role.TEACHER)
                .build();

        UserCredentials userSaved = userRepository.save(userCredentials);
        Teacher  teacher = Teacher.builder().school(school).credentials(userSaved).build();

        school.addTeacher(teacher);

        this.teacherRepository.save(teacher);
        this.schoolRepository.save(school);
        eventPublisher.publishEvent(new UserCreated(this,userSaved,language));
    }

    @Override
    public Page<Teacher> getPaginatedTeachers(int size, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, size);
        return this.teacherRepository.findAll(pageable);
    }

    @Override
    public Teacher findTeacherById(Long id) {
        return this.teacherRepository
                .findTeacherByIdWithSchoolAndCredentials(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public void updateTeacherUser(String email, String name, String language, Long schoolId, Boolean isActive, Long managerId) throws EmailAlreadyRegistered {
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

        School school = this.findSchoolById(schoolId);

        userCredentials.setEmail(email);
        userCredentials.setName(name);
        userCredentials.setIsActive(isActive);
        userRepository.save(userCredentials);

        Teacher teacher = this.teacherRepository.findById(managerId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        school.removeTeacher(teacher);
        school.addTeacher(teacher);

        this.teacherRepository.save(teacher);
        this.schoolRepository.save(school);
    }

    private School findSchoolById(Long schoolId){
        return this.schoolRepository
                .findById(schoolId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Não foi encontrada uma escola com este Id")
                );
    }
}
