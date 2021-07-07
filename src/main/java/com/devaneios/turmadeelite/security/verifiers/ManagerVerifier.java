package com.devaneios.turmadeelite.security.verifiers;

import com.devaneios.turmadeelite.entities.Manager;
import com.devaneios.turmadeelite.entities.School;
import com.devaneios.turmadeelite.entities.UserCredentials;
import com.devaneios.turmadeelite.repositories.ManagerRepository;
import com.devaneios.turmadeelite.security.AuthenticationInfo;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@AllArgsConstructor
public class ManagerVerifier implements ValidityVerifier{

    private final ManagerRepository managerRepository;
    private final AuthenticationInfo authenticationInfo;

    @Override
    public void verify() {
        Manager manager = this.managerRepository
                .findManagerByAuthUuidWithSchoolAndCredentials(authenticationInfo.getPrincipal())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        School school = manager.getSchool();
        UserCredentials credentials = manager.getCredentials();
        if(!school.getIsActive() || !credentials.getIsActive()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }
}
