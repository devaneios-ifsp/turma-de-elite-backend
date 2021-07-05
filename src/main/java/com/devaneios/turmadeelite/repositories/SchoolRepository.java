package com.devaneios.turmadeelite.repositories;

import com.devaneios.turmadeelite.entities.School;
import com.devaneios.turmadeelite.entities.UserCredentials;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchoolRepository extends CrudRepository<School,Long> {
    boolean existsByIdentifier(String identifier);
    Page<School> findAll(Pageable pageable);

    Optional<School> findByIdentifier(String identifier);
}
