package com.example.Petbulance_BE.domain.report.exception;

import java.time.LocalDateTime;

public class ReviewBannedException extends RuntimeException {

    private final LocalDateTime bannedUntil;

    public ReviewBannedException(LocalDateTime bannedUntil) {
        super("후기 이용이 " + bannedUntil + " 까지 정지되었습니다.");
        this.bannedUntil = bannedUntil;
    }

    public LocalDateTime getBannedUntil() {
        return bannedUntil;
    }
}

