package com.example.Petbulance_BE.domain.report.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReportActionType {

    WARNING("경고 조치"),   // 경고 조치
    SUSPEND("게시 정지 및 7일 이용정지 처분"),   // 게시 정지(차단)
    PUBLISH("게시 유지");    // 게시 유지(문제 없음)

    private final String description;
}
