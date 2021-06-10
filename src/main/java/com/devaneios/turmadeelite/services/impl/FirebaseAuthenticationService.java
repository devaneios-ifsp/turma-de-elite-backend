package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.security.AuthenticationInfo;
import com.devaneios.turmadeelite.services.AuthenticationService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FirebaseAuthenticationService implements AuthenticationService {

    @Autowired
    private final FirebaseApp firebaseApp;

    public FirebaseAuthenticationService(FirebaseApp firebaseApp) {
        this.firebaseApp = firebaseApp;
    }

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
        UserRecord user = FirebaseAuth.getInstance(firebaseApp).createUser(createRequest);
        return user.getUid();
    }
}
