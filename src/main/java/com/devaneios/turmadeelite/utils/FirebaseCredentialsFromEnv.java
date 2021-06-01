package com.devaneios.turmadeelite.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FirebaseCredentialsFromEnv {

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

    public String FirebaseAdminJsonFromEnv(){
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
}
