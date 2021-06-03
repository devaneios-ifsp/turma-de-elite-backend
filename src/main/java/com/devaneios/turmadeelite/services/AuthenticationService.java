package com.devaneios.turmadeelite.services;

import com.devaneios.turmadeelite.entities.Role;
import com.devaneios.turmadeelite.security.AuthenticationInfo;
import com.google.firebase.auth.FirebaseAuthException;

public interface AuthenticationService {
    AuthenticationInfo verifyTokenId(String token) throws FirebaseAuthException;
    String createUser(String email, String password) throws Exception;
}
