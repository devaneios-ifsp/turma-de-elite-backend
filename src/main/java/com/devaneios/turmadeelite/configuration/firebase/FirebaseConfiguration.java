package com.devaneios.turmadeelite.configuration.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;

@Slf4j
@Service
public class FirebaseConfiguration {

    @Value("${firebase.credentials.type}")
    private String type;

    @Value("${firebase.credentials.project-id}")
    private String projectId;

    @Value("${firebase.credentials.private-key-id}")
    private String privateKeyId;

    @Value("${firebase.credentials.private-key}")
    private String privateKey;

    @Value("${firebase.credentials.client-email}")
    private String clientEmail;

    @Value("${firebase.credentials.client-id}")
    private String clientId;

    @Value("${firebase.credentials.auth-uri}")
    private String authUri;

    @Value("${firebase.credentials.token-uri}")
    private String tokenUri;

    @Value("${firebase.credentials.auth-provider-cert-url}")
    private String authProviderCertUrl;

    @Value("${firebase.credentials.client-cert-url}")
    private String clientCertUrl;

    public String firebaseAdminJsonFromEnv(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        buffer.append("\"").append("type").append("\":\"").append(type).append("\",");
        buffer.append("\"").append("project_id").append("\":\"").append(projectId).append("\",");
        buffer.append("\"").append("private_key_id").append("\":\"").append(privateKeyId).append("\",");
        buffer.append("\"").append("private_key").append("\":\"").append(privateKey).append("\",");
        buffer.append("\"").append("client_email").append("\":\"").append(clientEmail).append("\",");
        buffer.append("\"").append("client_id").append("\":\"").append(clientId).append("\",");
        buffer.append("\"").append("auth_uri").append("\":\"").append(authUri).append("\",");
        buffer.append("\"").append("token_uri").append("\":\"").append(tokenUri).append("\",");
        buffer.append("\"").append("auth_provider_x509_cert_url").append("\":\"").append(authProviderCertUrl).append("\",");
        buffer.append("\"").append("client_x509_cert_url").append("\":\"").append(clientCertUrl).append("\"");
        buffer.append("}");
        return buffer.toString();
    }

    private FirebaseApp firebaseApp;

    public FirebaseConfiguration() throws IOException {
        try{
            File file = ResourceUtils.getFile("firebase/firebase-sdk-key.json");

            FileInputStream serviceAccount = new FileInputStream(file);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            this.firebaseApp = FirebaseApp.initializeApp(options);
        }catch (Exception e){
            String jsonCredentials = this.firebaseAdminJsonFromEnv();
            InputStream credentialsStream = new ByteArrayInputStream(jsonCredentials.getBytes());
            FirebaseOptions options = FirebaseOptions
                    .builder()
                    .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                    .build();
            this.firebaseApp = FirebaseApp.initializeApp(options);
        }
        log.info("Firebase SDK Admin has initialized with success!");
    }

    @Bean
    FirebaseApp getFirebaseApp(){
        return this.firebaseApp;
    }
}
