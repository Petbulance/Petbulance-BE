package com.example.Petbulance_BE.global.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.io.InputStream;

@Profile("prod")
public class FirebaseInitializer {

    public static void initialize() throws IOException {

        InputStream serviceAccount =
                new ClassPathResource("petbulance-b316f-firebase-adminsdk-fbsvc-8c92a7aab5.json")
                        .getInputStream();

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}
