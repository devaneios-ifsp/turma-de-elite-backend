package com.devaneios.turmadeelite.services;

public interface PasswordHasher {
    String createHash(String rawValue);

    boolean compareHashes(String hashedValue, String rawValue);
}
