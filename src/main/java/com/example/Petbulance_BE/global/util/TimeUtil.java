package com.example.Petbulance_BE.global.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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

    public static String formatCreatedAt(String createdAtString) {
        if (createdAtString == null || createdAtString.isBlank()) return "";

        LocalDateTime createdAt = null;
        DateTimeFormatter[] possibleFormats = {
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,            // "2025-11-06T21:30:00"
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"), // "2025-11-06 21:30:00"
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),    // "2025-11-06 21:30"
                DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"),    // "2025.11.06 21:30"
                DateTimeFormatter.ofPattern("yyyy.MM.dd")           // "2025.11.06"
        };

        for (DateTimeFormatter formatter : possibleFormats) {
            try {
                createdAt = LocalDateTime.parse(createdAtString, formatter);
                break;
            } catch (DateTimeParseException ignored) {}
        }

        if (createdAt == null) return ""; // 파싱 실패 시 빈 문자열 반환
        return formatCreatedAt(createdAt); // 기존 메서드 재사용
    }
}
