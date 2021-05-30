package com.devaneios.turmadeelite.infrastructure.exceptions;


import org.springframework.security.core.AuthenticationException;

public class BearerTokenNotFoundException extends AuthenticationException {
    public BearerTokenNotFoundException(){
        super("Impossível autenticar. Authorization Header não encontrado");
    }
}
