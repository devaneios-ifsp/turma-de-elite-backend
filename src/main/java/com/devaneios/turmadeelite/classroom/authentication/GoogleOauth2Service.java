package com.devaneios.turmadeelite.classroom.authentication;

import com.google.api.client.auth.oauth2.*;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.classroom.ClassroomScopes;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class GoogleOauth2Service {

    private static final String CREDENTIALS_FILE_PATH = "/classroom-credentials.json";
    public static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    public static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    GoogleAuthorizationCodeFlow authFlow;

    public GoogleOauth2Service() throws IOException {
        InputStream in = GoogleOauth2Service.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        this.authFlow = new GoogleAuthorizationCodeFlow
                .Builder(
                HTTP_TRANSPORT,
                JSON_FACTORY,
                clientSecrets,
                ClassroomScopes.all())
                .setDataStoreFactory(MemoryDataStoreFactory.getDefaultInstance())
                .setAccessType("offline")
                .build();
    }

    public String classroomAuth() throws IOException {
        GoogleAuthorizationCodeRequestUrl googleAuthorizationCodeRequestUrl = authFlow.newAuthorizationUrl();
        googleAuthorizationCodeRequestUrl.setRedirectUri("http://localhost:8080/Callback");
        return googleAuthorizationCodeRequestUrl.toURL().toString();
    }

    public void classroomCallback(String url, String authUUid) throws IOException {
        AuthorizationCodeResponseUrl authorizationCodeResponseUrl = new AuthorizationCodeResponseUrl(url);
        String code = authorizationCodeResponseUrl.getCode();
        GoogleTokenResponse execute = this.authFlow.newTokenRequest(code).setRedirectUri("http://localhost:8080/Callback").execute();
        this.authFlow.createAndStoreCredential(execute, authUUid);


    }

    public Credential getCredential(String authUUid) throws IOException {
        return this.authFlow.loadCredential(authUUid);
    }
}
