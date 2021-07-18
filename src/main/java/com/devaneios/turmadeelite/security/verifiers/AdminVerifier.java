package com.devaneios.turmadeelite.security.verifiers;

import com.devaneios.turmadeelite.entities.UserCredentials;
import com.devaneios.turmadeelite.exceptions.UnexpectedAuthenticationException;
import com.devaneios.turmadeelite.repositories.UserRepository;
import com.devaneios.turmadeelite.security.AuthenticationInfo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@AllArgsConstructor
public class AdminVerifier implements ValidityVerifier{

    private final UserRepository userRepository;
    private final AuthenticationInfo authenticationInfo;

    @Override
    public void verify() {
        UserCredentials credentials = this.userRepository
                .findByAuthUuid(authenticationInfo.getPrincipal())
                .orElseThrow(() -> new UnexpectedAuthenticationException());

        if(!credentials.getIsActive()){
            throw new UnexpectedAuthenticationException();
        }
    }
}
