package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.dto.FirstAccessDTO;
import com.devaneios.turmadeelite.entities.UserCredentials;
import com.devaneios.turmadeelite.exceptions.UserAlreadyDoFirstAccess;
import com.devaneios.turmadeelite.exceptions.UserNotFoundException;
import com.devaneios.turmadeelite.repositories.UserRepository;
import com.devaneios.turmadeelite.services.AdminFirstAccessService;
import com.devaneios.turmadeelite.services.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AdminFirstAccessImpl implements AdminFirstAccessService {

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    @Override
    @Transactional
    public void doFirstAccess(FirstAccessDTO firstAccessDTO) throws Exception {
        UserCredentials userCredentials = this.userRepository.findUserByEmailAndFirstAccessToken(
                firstAccessDTO.getEmail(),
                firstAccessDTO.getFirstAccessToken()
        ).orElseThrow(UserNotFoundException::new);
        if(userCredentials.getAuthUuid() != null){
            throw new UserAlreadyDoFirstAccess();
        }
        String uid = authenticationService.createUser(firstAccessDTO.getEmail(), firstAccessDTO.getPassword());
        userCredentials.setAuthUuid(uid);
        userRepository.save(userCredentials);
    }

    @Override
    public String verifyToken(String verifyToken) {
        UserCredentials userCredentials = this.userRepository
                .findByFirstAccessToken(verifyToken)
                .orElseThrow(UserNotFoundException::new);
        if(userCredentials.getAuthUuid() != null){
            throw new UserAlreadyDoFirstAccess();
        }else{
            return userCredentials.getEmail();
        }
    }
}
