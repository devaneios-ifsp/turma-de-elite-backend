package com.devaneios.turmadeelite.configuration.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class FirebaseConfiguration {

    private FirebaseApp firebaseApp;

    public FirebaseConfiguration() throws IOException {
        FileInputStream serviceAccount = new FileInputStream("./firebase-sdk-key.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        this.firebaseApp = FirebaseApp.initializeApp(options);
        log.info("Firebase SDK Admin has initialized with success!");
    }

    @Bean
    FirebaseApp getFirebaseApp(){
        return this.firebaseApp;
    }
}
