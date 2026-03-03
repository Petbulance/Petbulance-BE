package com.example.Petbulance_BE.global.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FcmService {

    public void sendPushNotification(String token, String title, String body, Map<String, String> data) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putAllData(data) // 상세 데이터(type, id 등)를 한꺼번에 넣음
                    .build();

            FirebaseMessaging.getInstance().send(message);
        } catch (Exception e) {
            // 로깅 처리
        }
    }
}