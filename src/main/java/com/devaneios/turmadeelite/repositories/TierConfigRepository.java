package com.devaneios.turmadeelite.repositories;

import com.devaneios.turmadeelite.entities.TierConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TierConfigRepository extends JpaRepository<TierConfig, Long> {
}
