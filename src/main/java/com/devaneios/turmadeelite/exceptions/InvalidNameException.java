package com.devaneios.turmadeelite.exceptions;

public class InvalidNameException extends IllegalArgumentException{

    public static void throwNullName(){
        throw new InvalidNameException("O nome não pode ser nulo");
    }

    public static void throwEmptyName(){
        throw new InvalidNameException("O nome não pode estar vazio");
    }

    public static void throwNumberName(){
        throw new InvalidNameException("O nome não pode possuir números");
    }

    private InvalidNameException(String message){
        super(message);
    }
}
