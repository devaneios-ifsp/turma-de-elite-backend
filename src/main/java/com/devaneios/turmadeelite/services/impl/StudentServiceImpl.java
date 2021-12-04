package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.dto.StudentCreateDTO;
import com.devaneios.turmadeelite.dto.UserActiveInactiveDTO;
import com.devaneios.turmadeelite.entities.School;
import com.devaneios.turmadeelite.entities.Student;
import com.devaneios.turmadeelite.entities.UserCredentials;
import com.devaneios.turmadeelite.events.UserCreated;
import com.devaneios.turmadeelite.exceptions.EmailAlreadyRegistered;
import com.devaneios.turmadeelite.repositories.LogStatusUserRepository;
import com.devaneios.turmadeelite.repositories.StudentRepository;
import com.devaneios.turmadeelite.repositories.UserRepository;
import com.devaneios.turmadeelite.services.SchoolService;
import com.devaneios.turmadeelite.services.StudentService;
import lombok.AllArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final UserRepository userRepository;
    private final SchoolService schoolService;
    private final StudentRepository studentRepository;
    private final LogStatusUserRepository logStatusUserRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public void createStudent(StudentCreateDTO studentDTO, String managerAuthUuid) {
        if(this.userRepository.existsByEmail(studentDTO.getEmail())){
            throw new EmailAlreadyRegistered();
        }

        School school = this.schoolService.findSchoolByManagerAuthUuid(managerAuthUuid);

        UserCredentials credentials = studentDTO.toCredentials();

        UserCredentials userSaved = this.userRepository.save(credentials);
        this.studentRepository.insertUserAsStudent(userSaved.getId(),school.getId(),studentDTO.getRegistry());
        this.eventPublisher.publishEvent(new UserCreated(this,userSaved, studentDTO.getLanguage()));
    }

    @Override
    public Page<Student> getPaginatedStudents(int size, int pageNumber, String managerAuthUuid) {
        Pageable pageable = PageRequest.of(pageNumber, size);
        School school = this.schoolService.findSchoolByManagerAuthUuid(managerAuthUuid);
        return this.studentRepository.findAllBySchoolId(school.getId(),pageable);
    }

    @Override
    @Transactional
    public void updateStudent(StudentCreateDTO studentCreateDTO, Long id, String managerAuthUuid) {
        Optional<UserCredentials> userCredentialsOptional = this.userRepository.findByEmail(studentCreateDTO.getEmail());
        UserCredentials credentials = null;

        if(userCredentialsOptional.isPresent()){
            credentials = userCredentialsOptional.get();
            if(!credentials.getEmail().equals(studentCreateDTO.getEmail())) throw new EmailAlreadyRegistered();
        }else{
            credentials = userCredentialsOptional.get();
        }

        Student student = null;
        School school = this.schoolService.findSchoolByManagerAuthUuid(managerAuthUuid);
        Optional<Student> optionalStudent = this.studentRepository.findByRegistryAndSchoolId(studentCreateDTO.getRegistry(), school.getId());
        if(optionalStudent.isPresent()){
            student = optionalStudent.get();
            if(!student.getRegistry().equals(studentCreateDTO.getRegistry())) throw new ResponseStatusException(HttpStatus.CONFLICT);
        }else{
            student = this.studentRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        }

        credentials.setEmail(studentCreateDTO.getEmail());
        credentials.setName(studentCreateDTO.getName());
        credentials.setIsActive(studentCreateDTO.getIsActive());
        this.userRepository.save(credentials);

        student.setRegistry(studentCreateDTO.getRegistry());

        this.studentRepository.save(student);

        logStatusUserRepository.insertLogStatusUser(credentials.getId(), !credentials.getIsActive());
    }

    @Override
    public Student findStudentById(Long id, String managerAuthUuid) {
        School school = this.schoolService.findSchoolByManagerAuthUuid(managerAuthUuid);
        return this.studentRepository
                .findByIdAndSchoolId(school.getId(),id)
                .orElseThrow(() -> new  ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public List<Student> findByStudentRegistrySimilarity(String registry, String managerAuthUuid) {
        School school = this.schoolService.findSchoolByManagerAuthUuid(managerAuthUuid);
        return this.studentRepository.findStudentsByRegistrySimilarity("%"+registry+"%",school.getId());
    }

    @Override
    public List<UserActiveInactiveDTO> getInactiveActiveStudents(String managerAuthUuid) {
        School school = this.schoolService.findSchoolByManagerAuthUuid(managerAuthUuid);

        DateTime date = DateTime.now();
        int month = date.getMonthOfYear();
        int year = date.getYear() - 1;

        int monthsInAYear = 12;

        List<UserActiveInactiveDTO> activeInactiveStudents = new ArrayList<>();

        for (int i = 0; i <= monthsInAYear; i++) {
            int activeUsers = 0;
            int inactiveUsers = 0;

            if(month > monthsInAYear) {
                month = 1;
                year += 1;
            }

            List<Integer> usersId = studentRepository.findBySchoolDate(month, year, school.getId());

            if(usersId != null && usersId.stream().count() > 0) {
                for (int id : usersId) {
                    if(!(logStatusUserRepository.getLastOldStatusUser(id)))
                        activeUsers += 1;
                    else
                        inactiveUsers += 1;
                }
            }

            UserActiveInactiveDTO user = new UserActiveInactiveDTO();

            user.setActiveUser(activeUsers);
            user.setInactiveUser(inactiveUsers);
            user.setMonth(month);
            user.setYear(year);

            activeInactiveStudents.add(user);

            month++;
        }
        return activeInactiveStudents;
    }
}
