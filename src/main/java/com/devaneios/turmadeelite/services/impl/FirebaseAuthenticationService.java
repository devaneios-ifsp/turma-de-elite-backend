package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.entities.Role;
import com.devaneios.turmadeelite.security.AuthenticationInfo;
import com.devaneios.turmadeelite.services.AuthenticationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;

@Service
public class FirebaseAuthenticationService implements AuthenticationService {
    @Override
    public AuthenticationInfo verifyTokenId(String token) throws FirebaseAuthException {
        FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(token);
        return new AuthenticationInfo(firebaseToken.getUid(), firebaseToken.getEmail(), true);
    }

    @Override
    public String createUser(String email, String password) throws Exception{
        UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest();
        createRequest.setEmail(email);
        createRequest.setPassword(password);
        UserRecord user = FirebaseAuth.getInstance().createUser(createRequest);
        return user.getUid();
    }
}
