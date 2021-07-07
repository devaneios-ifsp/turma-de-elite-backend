package com.devaneios.turmadeelite.security;

import com.devaneios.turmadeelite.security.verifiers.ValidityVerifier;
import com.devaneios.turmadeelite.security.verifiers.ValidityVerifierFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class FirebaseAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private ValidityVerifierFactory verifierFactory;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AuthenticationInfo authenticationInfo = (AuthenticationInfo) authentication;
        ValidityVerifier verifier = verifierFactory.fromAuthenticationInfo(authenticationInfo);
        verifier.verify();
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (AuthenticationInfo.class.isAssignableFrom(authentication));
    }
}
