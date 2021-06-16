package com.devaneios.turmadeelite.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class EmailAlreadyRegistered extends RuntimeException{
    public EmailAlreadyRegistered(){
        super("Este E-mail já está registrado na base de dados");
    }
}
