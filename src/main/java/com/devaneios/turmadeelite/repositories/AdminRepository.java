package com.devaneios.turmadeelite.repositories;

import com.devaneios.turmadeelite.entities.AdminUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AdminRepository extends CrudRepository<AdminUser,Long> {
    public Optional<AdminUser> findUserByEmail(String email);
}
