package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.exceptions.EmailAlreadyRegistered;

public interface AdminService {
    void createAdminUser(String email, String name, String language) throws EmailAlreadyRegistered;
}
