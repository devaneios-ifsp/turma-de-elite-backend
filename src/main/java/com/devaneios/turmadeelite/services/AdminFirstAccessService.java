package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.entities.AdminUser;
import com.devaneios.turmadeelite.exceptions.UserNotFoundException;

public interface AdminFirstAccessService {
    void doFirstAccess(String email,String authUuid) throws UserNotFoundException;
}
