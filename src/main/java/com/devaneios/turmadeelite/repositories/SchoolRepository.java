package com.devaneios.turmadeelite.repositories;

import com.devaneios.turmadeelite.entities.School;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolRepository extends CrudRepository<School,Long> {
    boolean existsByIdentifier(String identifier);
    Page<School> findAll(Pageable pageable);

    Optional<School> findByIdentifier(String identifier);

    List<School> findByNameContainingIgnoreCase(String name);
}
