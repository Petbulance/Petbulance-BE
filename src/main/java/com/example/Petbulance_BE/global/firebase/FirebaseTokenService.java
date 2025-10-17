package com.example.Petbulance_BE.global.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.stereotype.Service;

@Service
public class FirebaseTokenService {

    public String createCustomToken(String uid) throws FirebaseAuthException {
        // uid: 우리 DB에서 발급한 유저 ID (유일한 값)
        return FirebaseAuth.getInstance().createCustomToken(uid);
    }
}