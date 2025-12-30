package com.example.Petbulance_BE.global.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.ByteArrayInputStream;

@Configuration
@Profile("prod")
public class FirebaseInitializer {

    @Value("${FIREBASE_CREDENTIALS}")
    private String firebaseCredentials;

    @PostConstruct
    public void initialize() throws Exception {

        GoogleCredentials credentials =
                GoogleCredentials.fromStream(
                        new ByteArrayInputStream(firebaseCredentials.getBytes())
                );

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}
