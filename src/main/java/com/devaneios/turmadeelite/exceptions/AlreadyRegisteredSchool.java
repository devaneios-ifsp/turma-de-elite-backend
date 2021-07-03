package com.devaneios.turmadeelite.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class AlreadyRegisteredSchool extends RuntimeException{
    public AlreadyRegisteredSchool(String existsIdentifier){
        super("Já existe uma escola cadastrada com o identificador " + existsIdentifier);
    }
}
