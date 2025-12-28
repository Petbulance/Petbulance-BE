package com.example.Petbulance_BE.domain.report.exception;

import java.time.LocalDateTime;

public class CommunityBannedException extends RuntimeException {

    private final LocalDateTime bannedUntil;

    public CommunityBannedException(LocalDateTime bannedUntil) {
        super("커뮤니티 이용이 " + bannedUntil + " 까지 정지되었습니다.");
        this.bannedUntil = bannedUntil;
    }

    public LocalDateTime getBannedUntil() {
        return bannedUntil;
    }
}

