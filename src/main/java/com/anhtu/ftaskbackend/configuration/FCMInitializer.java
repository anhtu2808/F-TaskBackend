package com.anhtu.ftaskbackend.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class FCMInitializer {

    @Value("${firebase.project-id}")
    private String projectId;

    @Value("${firebase.private-key}")
    private String privateKey;

    @Value("${firebase.client-email}")
    private String clientEmail;

    @Value("${firebase.private-key-id}")
    private String privateKeyId;

    @Value("${firebase.client-id}")
    private String clientId;

    @PostConstruct
    public void initialize() {
        try {
            String formattedPrivateKey = privateKey.replace("\\n", "\n");

            String firebaseConfig = String.format("""
                            {
                              "type": "service_account",
                              "project_id": "%s",
                              "private_key_id": "%s",
                              "private_key": "%s",
                              "client_email": "%s",
                              "client_id": "%s",
                              "auth_uri": "https://accounts.google.com/o/oauth2/auth",
                              "token_uri": "https://oauth2.googleapis.com/token",
                              "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
                              "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/%s"
                            }
                            """,
                    projectId,
                    privateKeyId,
                    formattedPrivateKey,
                    clientEmail,
                    clientId,
                    clientEmail.replace("@", "%40")
            );

            InputStream serviceAccount =
                    new ByteArrayInputStream(firebaseConfig.getBytes(StandardCharsets.UTF_8));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase initialized successfully from environment variables");
            }
        } catch (Exception e) {
            log.error("Error initializing Firebase: {}", e.getMessage(), e);
        }
    }
}
