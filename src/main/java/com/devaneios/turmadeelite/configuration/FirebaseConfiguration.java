package com.devaneios.turmadeelite.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.Map;

@Slf4j
@Service
public class FirebaseConfiguration {

    public String firebaseAdminJsonFromEnv(){
        Map<String, String> env = System.getenv();
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        buffer.append("\"").append("type").append("\":\"").append(env.get("f_type")).append("\",");
        buffer.append("\"").append("project_id").append("\":\"").append(env.get("f_project_id")).append("\",");
        buffer.append("\"").append("private_key_id").append("\":\"").append(env.get("f_private_key_id")).append("\",");
        buffer.append("\"").append("private_key").append("\":\"").append(env.get("f_private_key")).append("\",");
        buffer.append("\"").append("client_email").append("\":\"").append(env.get("f_client_email")).append("\",");
        buffer.append("\"").append("client_id").append("\":\"").append(env.get("f_client_id")).append("\",");
        buffer.append("\"").append("auth_uri").append("\":\"").append(env.get("f_auth_uri")).append("\",");
        buffer.append("\"").append("token_uri").append("\":\"").append(env.get("f_token_uri")).append("\",");
        buffer.append("\"").append("auth_provider_x509_cert_url").append("\":\"").append(env.get("f_auth_provider_x509_cert_url")).append("\",");
        buffer.append("\"").append("client_x509_cert_url").append("\":\"").append(env.get("f_client_x509_cert_url")).append("\"");
        buffer.append("}");
        return buffer.toString();
    }

    private FirebaseApp firebaseApp;

    public FirebaseConfiguration() throws IOException {
        try{
            log.info("Initializing Firebase SDK Admin with Env Variables");
            String jsonCredentials = this.firebaseAdminJsonFromEnv();
            InputStream credentialsStream = new ByteArrayInputStream(jsonCredentials.getBytes());
            FirebaseOptions options = FirebaseOptions
                    .builder()
                    .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                    .build();
            this.firebaseApp = FirebaseApp.initializeApp(options);
            log.info("Firebase SDK Admin has initialized with success!");

        }catch (Exception e){
            log.info("Initializing Firebase SDK Admin with Env Variables failed");
            log.info("Initializing Firebase SDK Admin with File");
            File file = ResourceUtils.getFile("firebase/firebase-sdk-key.json");
            FileInputStream serviceAccount = new FileInputStream(file);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            this.firebaseApp = FirebaseApp.initializeApp(options);
            log.info("Firebase SDK Admin has initialized with success!");
        }

    }

    @Bean
    FirebaseApp getFirebaseApp(){
        return this.firebaseApp;
    }
}
