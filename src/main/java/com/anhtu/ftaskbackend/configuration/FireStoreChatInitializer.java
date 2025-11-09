package com.anhtu.ftaskbackend.configuration;

import com.anhtu.ftaskbackend.configuration.ConfigurationProperties.FirestoreProperties;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
@RequiredArgsConstructor
public class FireStoreChatInitializer {

    private final FirestoreProperties firestoreProperties;
    public static final String FIRESTORE_APP_NAME = "firestoreApp";

    @PostConstruct
    public void init() {
        try {
            String fixedKey = firestoreProperties.getPrivateKey()
                    .replace("\\n", "\n")
                    .replaceAll("^'|'$", "");

            String credentialsJson = String.format("""
                            {
                              "type": "%s",
                              "project_id": "%s",
                              "private_key_id": "%s",
                              "private_key": "%s",
                              "client_email": "%s",
                              "client_id": "%s",
                              "auth_uri": "%s",
                              "token_uri": "%s",
                              "auth_provider_x509_cert_url": "%s",
                              "client_x509_cert_url": "%s",
                              "universe_domain": "%s"
                            }
                            """,
                    firestoreProperties.getType(),
                    firestoreProperties.getProjectId(),
                    firestoreProperties.getPrivateKeyId(),
                    fixedKey,
                    firestoreProperties.getClientEmail(),
                    firestoreProperties.getClientId(),
                    firestoreProperties.getAuthUrl(),
                    firestoreProperties.getTokenUri(),
                    firestoreProperties.getAuthCertUrl(),
                    firestoreProperties.getClientCertUrl(),
                    firestoreProperties.getUniverseDomain()
            );

            ByteArrayInputStream serviceAccount =
                    new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId(firestoreProperties.getProjectId())
                    .build();

            if (FirebaseApp.getApps().stream().noneMatch(app -> app.getName().equals(FIRESTORE_APP_NAME))) {
                FirebaseApp.initializeApp(options, FIRESTORE_APP_NAME);
                System.out.println("✅ Firestore initialized successfully via environment configuration!");
            }

        } catch (Exception e) {
            e.printStackTrace(); // thêm dòng này để xem nguyên nhân thật
            throw new RuntimeException("🔥 Failed to initialize Firestore", e);
        }
    }

}
