package com.devaneios.turmadeelite.domain.values;

import com.devaneios.turmadeelite.domain.exceptions.InvalidPasswordException;
import com.devaneios.turmadeelite.domain.services.PasswordHasher;

public class Password {
    private String password;
    private PasswordHasher passwordHasher;

    public static Password createFromRawValue(String rawValue,PasswordHasher passwordHasher){
        if(rawValue == null) InvalidPasswordException.passwordIsNull();
        if(rawValue.trim().isEmpty()) InvalidPasswordException.passwordIsBlank();
        if(rawValue.trim().length() < 6) InvalidPasswordException.passwordIsTooWeak("- Possui menos de 6 caracteres");
        String hashedValue = passwordHasher.createHash(rawValue);
        return new Password(hashedValue,passwordHasher);
    }

    public static Password createFromHashedValue(String hashedValue, PasswordHasher passwordHasher){
        return new Password(hashedValue,passwordHasher);
    }

    private Password(String value, PasswordHasher passwordHasher){
        this.password = value;
        this.passwordHasher = passwordHasher;
    }

    public boolean matchWith(String rawValue){
        return this.passwordHasher.compareHashes(this.password,rawValue);
    }
}
