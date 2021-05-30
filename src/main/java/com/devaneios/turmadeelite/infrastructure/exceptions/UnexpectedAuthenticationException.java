package com.devaneios.turmadeelite.infrastructure.exceptions;


import org.springframework.security.core.AuthenticationException;

public class UnexpectedAuthenticationException extends AuthenticationException {
    public UnexpectedAuthenticationException(){
        super("Erro inesperado. Não foi possível autenticar");
    }
}
