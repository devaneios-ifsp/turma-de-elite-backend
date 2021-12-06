package com.devaneios.turmadeelite.external.classroom.authentication;

import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.classroom.ClassroomScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;

@Service
public class GoogleOauth2Service {

    private static final String CREDENTIALS_FILE_PATH = "classroom-credentials.json";
    public static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    public static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    private final String applicationAddress;

    GoogleAuthorizationCodeFlow authFlow;

    public GoogleOauth2Service(@Value("${application-address}")String applicationAddress) throws IOException {
        File file = ResourceUtils.getFile("classroom-credentials.json");
        InputStream in = new FileInputStream(file);
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

        this.applicationAddress = applicationAddress;
    }

    public String classroomAuth(String authUuid) throws IOException {
        GoogleAuthorizationCodeRequestUrl googleAuthorizationCodeRequestUrl = authFlow.newAuthorizationUrl();
        googleAuthorizationCodeRequestUrl.setRedirectUri(applicationAddress + "/Callback");
        return googleAuthorizationCodeRequestUrl.setState(authUuid).toURL().toString();
    }

    public void classroomCallback(String url, String authUUid) throws IOException {
        AuthorizationCodeResponseUrl authorizationCodeResponseUrl = new AuthorizationCodeResponseUrl(url);
        String code = authorizationCodeResponseUrl.getCode();
        GoogleTokenResponse execute = this.authFlow.newTokenRequest(code).setRedirectUri(applicationAddress + "/Callback").execute();
        this.authFlow.createAndStoreCredential(execute, authUUid);
    }

    public Credential getCredential(String authUUid) throws IOException {
        return this.authFlow.loadCredential(authUUid);
    }
}
