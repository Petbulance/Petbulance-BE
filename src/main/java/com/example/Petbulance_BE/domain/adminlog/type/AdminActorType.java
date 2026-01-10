package com.example.Petbulance_BE.domain.adminlog.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdminActorType {

    ADMIN("관리자"),   // 실제 관리자
    SYSTEM("시스템");  // 배치, 자동 처리

    private final String description;
}
