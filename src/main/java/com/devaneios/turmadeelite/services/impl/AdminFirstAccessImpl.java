package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.dto.FirstAccessDTO;
import com.devaneios.turmadeelite.entities.AdminUser;
import com.devaneios.turmadeelite.exceptions.UserNotFoundException;
import com.devaneios.turmadeelite.repositories.AdminRepository;
import com.devaneios.turmadeelite.services.AdminFirstAccessService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AdminFirstAccessImpl implements AdminFirstAccessService {

    private final AdminRepository adminRepository;

    @Override
    public void doFirstAccess(FirstAccessDTO firstAccessDTO, String authUuid) throws UserNotFoundException {
        AdminUser adminUser = this.adminRepository.findUserByEmailAndFirstAccessToken(firstAccessDTO.getEmail(),firstAccessDTO.getFirstAccessToken()).orElseThrow(UserNotFoundException::new);
        adminUser.setFirebaseUuid(authUuid);
        adminRepository.save(adminUser);
    }
}
