package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.dto.FirstAccessDTO;
import com.devaneios.turmadeelite.entities.UserCredentials;
import com.devaneios.turmadeelite.exceptions.UserAlreadyDoFirstAccess;
import com.devaneios.turmadeelite.exceptions.UserNotFoundException;
import com.devaneios.turmadeelite.repositories.AdminRepository;
import com.devaneios.turmadeelite.services.AdminFirstAccessService;
import com.devaneios.turmadeelite.services.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class AdminFirstAccessImpl implements AdminFirstAccessService {

    private final AdminRepository adminRepository;
    private final AuthenticationService authenticationService;

    @Override
    @Transactional
    public void doFirstAccess(FirstAccessDTO firstAccessDTO) throws Exception {
        UserCredentials userCredentials = this.adminRepository.findUserByEmailAndFirstAccessToken(
                firstAccessDTO.getEmail(),
                firstAccessDTO.getFirstAccessToken()
        ).orElseThrow(UserNotFoundException::new);
        if(userCredentials.getAuthUuid() != null){
            throw new UserAlreadyDoFirstAccess();
        }
        String uid = authenticationService.createUser(firstAccessDTO.getEmail(), firstAccessDTO.getPassword());
        userCredentials.setAuthUuid(uid);
        adminRepository.save(userCredentials);
    }

    @Override
    public void verifyToken(String verifyToken) {
        UserCredentials userCredentials = this.adminRepository
                .findByFirstAccessToken(verifyToken)
                .orElseThrow(UserNotFoundException::new);
        if(userCredentials.getAuthUuid() != null){
            throw new UserAlreadyDoFirstAccess();
        }
    }
}
