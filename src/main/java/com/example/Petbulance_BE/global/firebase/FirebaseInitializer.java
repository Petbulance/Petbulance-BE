package com.example.Petbulance_BE.global.firebase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Profile;

@Profile("prod")
public class FirebaseInitializer {
    public static void initialize() throws IOException {
        FileInputStream serviceAccount = new FileInputStream("/home/ubuntu/Petbulance-BE/src/main/resources/petbulance-b316f-firebase-adminsdk-fbsvc-8c92a7aab5.json");
//      /home/ubuntu/Petbulance-BE/src/main/resources/petbulance-b316f-firebase-adminsdk-fbsvc-8c92a7aab5.json
        //src/main/resources/petbulance-b316f-firebase-adminsdk-fbsvc-8c92a7aab5.json
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if(FirebaseApp.getApps().isEmpty()){
            FirebaseApp.initializeApp(options);
        }
    }
}
