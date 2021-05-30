package com.devaneios.turmadeelite.domain.values;

import com.devaneios.turmadeelite.domain.exceptions.InvalidNameException;

public class Name {
    private String name;

    public Name(String name) {
        if(name == null) InvalidNameException.throwNullName();
        if(name.isEmpty()) InvalidNameException.throwEmptyName();
        if(name.matches("[0-9]+\t")) InvalidNameException.throwNumberName();
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
