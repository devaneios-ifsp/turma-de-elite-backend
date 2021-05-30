package com.devaneios.turmadeelite.infrastructure.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class FirebaseAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        FirebaseAuthenticationInfo authenticationInfo = (FirebaseAuthenticationInfo) authentication;
        return authenticationInfo;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (FirebaseAuthenticationInfo.class.isAssignableFrom(authentication));
    }
}
