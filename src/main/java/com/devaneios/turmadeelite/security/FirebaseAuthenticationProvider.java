package com.devaneios.turmadeelite.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class FirebaseAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AuthenticationInfo authenticationInfo = (AuthenticationInfo) authentication;
        return authenticationInfo;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (AuthenticationInfo.class.isAssignableFrom(authentication));
    }
}
