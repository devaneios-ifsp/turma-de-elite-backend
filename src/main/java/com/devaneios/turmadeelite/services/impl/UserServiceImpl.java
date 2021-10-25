package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.dto.UserActiveInactiveDTO;
import com.devaneios.turmadeelite.dto.UserCredentialsCreateDTO;
import com.devaneios.turmadeelite.entities.Achievement;
import com.devaneios.turmadeelite.entities.Role;
import com.devaneios.turmadeelite.entities.UserCredentials;
import com.devaneios.turmadeelite.events.UserCreated;
import com.devaneios.turmadeelite.exceptions.EmailAlreadyRegistered;
import com.devaneios.turmadeelite.repositories.LogStatusUserRepository;
import com.devaneios.turmadeelite.repositories.UserRepository;
import com.devaneios.turmadeelite.services.UserService;
import lombok.AllArgsConstructor;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final LogStatusUserRepository logStatusUserRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void createAdminUser(String email, String name, Boolean isActive, String language) throws EmailAlreadyRegistered {
        if(this.userRepository.existsByEmail(email)){
            throw new EmailAlreadyRegistered();
        }
        UserCredentials userCredentials = UserCredentials
                .builder()
                .email(email)
                .firstAccessToken(UUID.randomUUID().toString())
                .name(name)
                .isActive(isActive)
                .role(Role.ADMIN)
                .build();
        UserCredentials userSaved = userRepository.save(userCredentials);

        logStatusUserRepository.insertLogStatusUser(userSaved.getId(), !userSaved.getIsActive());
        eventPublisher.publishEvent(new UserCreated(this,userSaved,language));
    }

    @Override
    public Page<UserCredentials> getPaginatedAdminUsers(int size, int pageNumber){
        PageRequest pageRequest = PageRequest.of(pageNumber, size);
        Page<UserCredentials> allAdmins = this.userRepository.findAllAdmins(pageRequest);
        return allAdmins;
    }

    @Override
    public UserCredentials findAdminById(Long userId) {
        return this.userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public void updateAdminUser(Long userId, UserCredentialsCreateDTO admin) {
        Optional<UserCredentials> byEmail = this.userRepository.findByEmail(admin.getEmail());
        byEmail.ifPresent(userCredentials -> {
            if(userCredentials.getId() != userId){
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            }
        });
        UserCredentials userCredentials = this.userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        userCredentials.setEmail(admin.getEmail());
        userCredentials.setName(admin.getName());
        userCredentials.setIsActive(admin.getIsActive());
        this.userRepository.save(userCredentials);
    }

    @Override
    public List<UserCredentials> getUsersByNameSimilarity(String name) {
        return this.userRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<UserActiveInactiveDTO> getInactivesActivesUsers() {
        DateTime date = DateTime.now();
        int month = date.getMonthOfYear();
        int year = date.getYear() - 1;

        List<UserActiveInactiveDTO> activeInactiveUsers = new ArrayList<>();

        for (int i = 0; i < 13; i++) {
            if(month > 12) {
                month = 1;
                year += 1;
            }

            UserActiveInactiveDTO user = new UserActiveInactiveDTO();

            user.setActiveUser(logStatusUserRepository.countActiveUsers(month, year));
            user.setInactiveUser(logStatusUserRepository.countInactiveUsers(month, year));
            user.setMonth(month);

            activeInactiveUsers.add(user);

            month++;
        }

        return activeInactiveUsers;
    }
}
