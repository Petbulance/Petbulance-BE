package com.example.Petbulance_BE.domain.adminlog.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdminPageType {

    SECURITY_ACCOUNT("보안/계정"),
    DASHBOARD("대시보드"),

    USER_MANAGEMENT("유저 관리"),
    HOSPITAL_MANAGEMENT("병원 관리"),
    REVIEW_MANAGEMENT("리뷰 관리"),
    COMMUNITY_MANAGEMENT("커뮤니티 관리"),

    CUSTOMER_CENTER("고객센터"),
    CONTENT_MANAGEMENT("콘텐츠 관리"),

    ADMIN_MANAGEMENT("관리자 관리"),
    ACTION_LOG("행동 로그"),

    SYSTEM("시스템");

    private final String description;
}
