package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.entities.Manager;
import com.devaneios.turmadeelite.entities.Role;
import com.devaneios.turmadeelite.entities.UserCredentials;
import com.devaneios.turmadeelite.events.UserCreated;
import com.devaneios.turmadeelite.exceptions.EmailAlreadyRegistered;
import com.devaneios.turmadeelite.repositories.ManagerRepository;
import com.devaneios.turmadeelite.repositories.UserRepository;
import com.devaneios.turmadeelite.services.ManagerService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ManagerServiceImpl implements ManagerService {

    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void createManagerUser(String email, String name, String language) throws EmailAlreadyRegistered {
        if(this.userRepository.existsByEmail(email)){
            throw new EmailAlreadyRegistered();
        }
        UserCredentials userCredentials = UserCredentials
                .builder()
                .email(email)
                .firstAccessToken(UUID.randomUUID().toString())
                .name(name)
                .role(Role.MANAGER)
                .build();
        UserCredentials userSaved = userRepository.save(userCredentials);
        Manager manager = Manager.builder().credentials(userSaved).build();
        this.managerRepository.save(manager);
        eventPublisher.publishEvent(new UserCreated(this,userSaved,language));
    }
}
