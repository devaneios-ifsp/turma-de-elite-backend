package com.devaneios.turmadeelite.repositories;

import com.devaneios.turmadeelite.entities.Manager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ManagerRepository extends CrudRepository<Manager,Long> {
    @Query("SELECT m FROM Manager m INNER JOIN m.credentials c WHERE role='MANAGER'")
    Page<Manager> findAllManagers(PageRequest pageRequest);

    @Query("SELECT m FROM Manager m JOIN m.credentials c JOIN m.school s WHERE m.id=:id")
    Optional<Manager> findManagerByIdWithSchoolAndCredentials(Long id);

    @Query("SELECT m FROM Manager m JOIN m.credentials c JOIN m.school s WHERE c.authUuid=:principal")
    Optional<Manager> findManagerByAuthUuidWithSchoolAndCredentials(String principal);

    @Query("SELECT m FROM Manager m JOIN m.credentials c WHERE c.name LIKE :name%")
    Optional<List<Manager>> findByNameContainingIgnoreCase(String name);
}
