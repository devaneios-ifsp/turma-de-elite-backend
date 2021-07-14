package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.entities.Manager;
import com.devaneios.turmadeelite.entities.Role;
import com.devaneios.turmadeelite.entities.School;
import com.devaneios.turmadeelite.entities.UserCredentials;
import com.devaneios.turmadeelite.events.UserCreated;
import com.devaneios.turmadeelite.exceptions.EmailAlreadyRegistered;
import com.devaneios.turmadeelite.repositories.ManagerRepository;
import com.devaneios.turmadeelite.repositories.SchoolRepository;
import com.devaneios.turmadeelite.repositories.UserRepository;
import com.devaneios.turmadeelite.services.ManagerService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ManagerServiceImpl implements ManagerService {

    private final ManagerRepository managerRepository;
    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public void createManagerUser(String email, String name, String language, Long schoolId, Boolean isActive) throws EmailAlreadyRegistered {
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
                .role(Role.MANAGER)
                .build();

        UserCredentials userSaved = userRepository.save(userCredentials);
        Manager manager = Manager.builder().school(school).credentials(userSaved).build();
        school.addManager(manager);
        this.managerRepository.save(manager);
        this.schoolRepository.save(school);
        eventPublisher.publishEvent(new UserCreated(this,userSaved,language));
    }

    @Override
    public Page<Manager> getPaginatedSchools(int size, int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber, size);
        return this.managerRepository.findAllManagers(pageRequest);
    }

    @Override
    public Manager findManagerById(Long id) {
        return this.managerRepository
                .findManagerByIdWithSchoolAndCredentials(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    @Override
    public void updateManagerUser(String email, String name, String language, Long schoolId, Boolean isActive,Long managerId) throws EmailAlreadyRegistered {
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

        Manager manager = this.managerRepository.findById(managerId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        school.removeManager(manager);
        school.addManager(manager);

        this.managerRepository.save(manager);
        this.schoolRepository.save(school);
    }

    @Override
    public Optional<List<Manager>> getManagersByNameSimilarity(String name) {
        return this.managerRepository.findByNameContainingIgnoreCase(name);
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
