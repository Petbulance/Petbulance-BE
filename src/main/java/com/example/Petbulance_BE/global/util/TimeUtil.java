package com.example.Petbulance_BE.global.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    public static String formatCreatedAt(LocalDateTime createdAt) {
        if (createdAt == null) return "";
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(createdAt, now);

        long minutes = duration.toMinutes();
        long hours = duration.toHours();

        if (minutes < 1) return "방금 전";
        if (minutes < 60) return minutes + "분 전";
        if (hours < 24) return hours + "시간 전";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return createdAt.format(dateFormatter);
    }
}
