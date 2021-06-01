package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.entities.AdminUser;
import com.devaneios.turmadeelite.exceptions.UserNotFoundException;
import com.devaneios.turmadeelite.repositories.AdminRepository;
import com.devaneios.turmadeelite.services.AdminFirstAccessService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AdminFirstAccessImpl implements AdminFirstAccessService {

    private final AdminRepository adminRepository;

    @Override
    public void doFirstAccess(String email, String authUuid) throws UserNotFoundException {
        AdminUser adminUser = this.adminRepository.findUserByEmail(email).orElseThrow(UserNotFoundException::new);
        adminUser.setFirebaseUuid(authUuid);
        adminRepository.save(adminUser);
    }
}
