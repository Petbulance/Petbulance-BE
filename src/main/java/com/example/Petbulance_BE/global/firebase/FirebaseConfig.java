package com.example.Petbulance_BE.global.firebase;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Configuration
@Profile("prod")
public class FirebaseConfig {
    @PostConstruct
    public void init() throws IOException{
        FirebaseInitializer.initialize();
    }
}