package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.dto.AdminCreateDTO;
import com.devaneios.turmadeelite.entities.Role;
import com.devaneios.turmadeelite.entities.UserCredentials;
import com.devaneios.turmadeelite.events.UserCreated;
import com.devaneios.turmadeelite.exceptions.EmailAlreadyRegistered;
import com.devaneios.turmadeelite.repositories.AdminRepository;
import com.devaneios.turmadeelite.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final AdminRepository adminRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void createAdminUser(String email, String name, String language) throws EmailAlreadyRegistered {
        if(this.adminRepository.existsByEmail(email)){
            throw new EmailAlreadyRegistered();
        }
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

    @Override
    public Page<UserCredentials> getPaginatedAdminUsers(int size, int pageNumber){
        PageRequest pageRequest = PageRequest.of(pageNumber, size);
        Page<UserCredentials> allAdmins = this.adminRepository.findAllAdmins(pageRequest);
        return allAdmins;
    }

    @Override
    public UserCredentials findAdminById(Long userId) {
        return this.adminRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public void updateAdminUser(Long userId, AdminCreateDTO admin) {
        Optional<UserCredentials> byEmail = this.adminRepository.findByEmail(admin.getEmail());
        byEmail.ifPresent(userCredentials -> {
            if(userCredentials.getId() != userId){
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            }
        });
        UserCredentials userCredentials = this.adminRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        userCredentials.setEmail(admin.getEmail());
        userCredentials.setName(admin.getName());
        this.adminRepository.save(userCredentials);
    }
}
