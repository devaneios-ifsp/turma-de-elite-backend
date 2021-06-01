package com.devaneios.turmadeelite.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.Collection;

public class FirebaseAuthenticationInfo implements Authentication {

    private final String userUuid;
    private final String userEmail;
    private final boolean isAuthenticated;

    public FirebaseAuthenticationInfo(String userUuid, String userEmail, boolean isAuthenticated) {
        this.userUuid = userUuid;
        this.userEmail = userEmail;
        this.isAuthenticated = isAuthenticated;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public String getPrincipal() {
        return this.userUuid;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean implies(Subject subject) {
        return Authentication.super.implies(subject);
    }
}
