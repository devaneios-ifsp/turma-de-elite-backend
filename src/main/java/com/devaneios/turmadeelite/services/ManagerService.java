package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.entities.UserCredentials;
import com.devaneios.turmadeelite.exceptions.EmailAlreadyRegistered;

public interface ManagerService {
    void createManagerUser(String email, String name, String language) throws EmailAlreadyRegistered;
}
