package com.devaneios.turmadeelite.domain.exceptions;

public class InvalidPasswordException extends IllegalArgumentException{

    public static void passwordIsNull(){
        throw new InvalidPasswordException("A senha não pode ser nula");
    }

    public static void passwordIsBlank(){
        throw new InvalidPasswordException("A senha não pode ser vazia");
    }

    public static void passwordIsTooWeak(String reasons){
        throw new InvalidPasswordException("A senha é muito fraca. Motivos:\n" + reasons);
    }

    private InvalidPasswordException(String message){
        super(message);
    }
}
