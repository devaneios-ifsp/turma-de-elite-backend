package com.devaneios.turmadeelite.infrastructure.firebase;

import com.devaneios.turmadeelite.infrastructure.utils.FirebaseCredentialsFromEnv;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@Slf4j
public class FirebaseConfiguration {

    private FirebaseApp firebaseApp;

    public FirebaseConfiguration(FirebaseCredentialsFromEnv firebaseCredentialsFromEnv) throws IOException {
        String jsonCredentials = firebaseCredentialsFromEnv.FirebaseAdminJsonFromEnv();
        InputStream credentialsStream = new ByteArrayInputStream(jsonCredentials.getBytes());
        FirebaseOptions options = FirebaseOptions
                .builder()
                .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                .build();
        this.firebaseApp = FirebaseApp.initializeApp(options);
        log.info("Firebase SDK Admin has initialized with success!");
    }

    @Bean
    FirebaseApp getFirebaseApp(){
        return this.firebaseApp;
    }
}
