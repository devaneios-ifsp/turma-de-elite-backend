package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.entities.Role;
import com.devaneios.turmadeelite.entities.UserCredentials;
import com.devaneios.turmadeelite.events.UserCreated;
import com.devaneios.turmadeelite.exceptions.EmailAlreadyRegistered;
import com.devaneios.turmadeelite.repositories.AdminRepository;
import com.devaneios.turmadeelite.services.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void createAdminUser(String email, String name, String language) throws EmailAlreadyRegistered {
        UserCredentials userCredentials = UserCredentials
                .builder()
                .email(email)
                .firstAccessToken(UUID.randomUUID().toString())
                .name(name)
                .role(Role.ADMIN)
                .build();
        UserCredentials userSaved = adminRepository.save(userCredentials);
        eventPublisher.publishEvent(new UserCreated(this,userSaved,language));
    }
}
