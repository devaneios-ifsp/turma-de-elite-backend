package com.devaneios.turmadeelite.exceptions;

public class UserNotFoundException extends IllegalArgumentException{
    public UserNotFoundException(){
        super("Não foi encontrado o usuário especificado");
    }
}
