package com.devaneios.turmadeelite.repositories;

import com.devaneios.turmadeelite.entities.UserCredentials;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AdminRepository extends CrudRepository<UserCredentials,Long> {
    public Optional<UserCredentials> findUserByEmailAndFirstAccessToken(String email, String firstAccessToken);

    Optional<UserCredentials> findByFirstAccessToken(String firstAccessToken);

    Optional<UserCredentials> findByAuthUuid(String authUuid);
}
