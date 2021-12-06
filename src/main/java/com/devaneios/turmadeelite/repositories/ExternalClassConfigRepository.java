package com.devaneios.turmadeelite.repositories;

import com.devaneios.turmadeelite.entities.ExternalClassConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExternalClassConfigRepository extends JpaRepository<ExternalClassConfig, Long> {
    Optional<ExternalClassConfig> findByExternalClassId(String courseId);
}