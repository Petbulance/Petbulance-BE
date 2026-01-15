package com.example.Petbulance_BE.domain.admin.user.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReviewBannedException extends RuntimeException {

    private final LocalDateTime bannedUntil;

    public ReviewBannedException(LocalDateTime bannedUntil) {
        super("리뷰 이용이 " + bannedUntil.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " 까지 정지되었습니다.");
        this.bannedUntil = bannedUntil;
    }

    public LocalDateTime getBannedUntil() {
        return bannedUntil;
    }
}
