package com.devaneios.turmadeelite.security;

import com.devaneios.turmadeelite.entities.Role;
import lombok.Data;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.security.auth.Subject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Setter
public class AuthenticationInfo implements Authentication {

    private final String userUuid;
    private final String userEmail;
    private final boolean isAuthenticated;
    private Role role;

    public AuthenticationInfo(String userUuid, String userEmail, boolean isAuthenticated) {
        this.userUuid = userUuid;
        this.userEmail = userEmail;
        this.isAuthenticated = isAuthenticated;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(this.role.name()));
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
        return this.isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return this.userEmail;
    }

    @Override
    public boolean implies(Subject subject) {
        return Authentication.super.implies(subject);
    }
}
