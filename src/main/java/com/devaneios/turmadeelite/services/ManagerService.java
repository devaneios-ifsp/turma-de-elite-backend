package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.entities.Manager;
import com.devaneios.turmadeelite.exceptions.EmailAlreadyRegistered;
import org.springframework.data.domain.Page;

public interface ManagerService {
    void createManagerUser(String email, String name, String language, Long schoolId, Boolean isActive) throws EmailAlreadyRegistered;

    Page<Manager> getPaginatedSchools(int size, int pageNumber);

    Manager findManagerById(Long id);

    void updateManagerUser(String email, String name, String language, Long schoolId, Boolean isActive,Long managerId) throws EmailAlreadyRegistered;
}
