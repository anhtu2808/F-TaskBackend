package com.anhtu.ftaskbackend.configuration.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "firestore")
public class FirestoreProperties {
    private String type;
    private String projectId;
    private String privateKeyId;
    private String privateKey;
    private String clientEmail;
    private String clientId;
    private String authUrl;
    private String tokenUri;
    private String authCertUrl;
    private String clientCertUrl;
    private String universeDomain;
}
