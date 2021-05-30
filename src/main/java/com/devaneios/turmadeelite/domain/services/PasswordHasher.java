package com.devaneios.turmadeelite.domain.services;

public interface PasswordHasher {
    String createHash(String rawValue);

    boolean compareHashes(String hashedValue, String rawValue);
}
