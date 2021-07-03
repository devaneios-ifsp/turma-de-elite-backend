package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.dto.AdminCreateDTO;
import com.devaneios.turmadeelite.entities.UserCredentials;
import com.devaneios.turmadeelite.exceptions.EmailAlreadyRegistered;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    void createAdminUser(String email, String name, String language) throws EmailAlreadyRegistered;
    Page<UserCredentials> getPaginatedAdminUsers(int size, int pageNumber);
    UserCredentials findAdminById(Long userId);
    void updateAdminUser(Long userId, AdminCreateDTO admin);
}
