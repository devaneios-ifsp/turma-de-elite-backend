package com.devaneios.turmadeelite.services.impl;

import com.devaneios.turmadeelite.dto.CustomTokenIdRequestDTO;
import com.devaneios.turmadeelite.dto.FirebaseCustomTokenIdDTO;
import com.devaneios.turmadeelite.security.AuthenticationInfo;
import com.devaneios.turmadeelite.services.AuthenticationService;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URL;

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

    @SneakyThrows
    public String createTokenFrom(String email,String password) throws FirebaseAuthException {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN,true);
        String url = "http://localhost:9099/identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=AIzaSyAXIaX1Y2g_jnIrvrewlVvmhR4iwpejxEc";
        CustomTokenIdRequestDTO request = new CustomTokenIdRequestDTO();
        request.setEmail(email);
        request.setPassword(password);
        RequestEntity<String> requestEntity = RequestEntity
                                                .post(new URL(url)
                                                .toURI())
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .body(mapper.writeValueAsString(request));

        ResponseEntity<FirebaseCustomTokenIdDTO> response = restTemplate.exchange(requestEntity,FirebaseCustomTokenIdDTO.class);
        return response.getBody().getIdToken();
    }
}
