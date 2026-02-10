package com.example.Petbulance_BE.domain.adminlog.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdminTargetType {

    /* ===== 시스템 ===== */
    SYSTEM("시스템"),

    /* ===== 계정 ===== */
    ACCOUNT("계정"),

    /* ===== 대시보드 ===== */
    DASHBOARD("대시보드"),

    /* ===== 유저 ===== */
    USER_LIST("유저 목록"),
    USER_INFO("유저 상세 정보"),

    /* ===== 병원 ===== */
    HOSPITAL_LIST("병원 목록"),
    HOSPITAL_DETAIL("병원 상세"),
    HOSPITAL_INFO("병원 기본 정보"),
    HOSPITAL_LOCATION("병원 위치"),
    HOSPITAL_HOURS("병원 운영시간"),
    HOSPITAL_TAG("병원 태그"),
    HOSPITAL_INTRO("병원 소개"),

    /* ===== 리뷰 ===== */
    REVIEW_LIST("리뷰 목록"),
    REVIEW_ACTION("리뷰 조치"),
    REVIEW_DETAIL("리뷰 상세"),
    REVIEW_STATUS("리뷰 상태 변경"),

    /* ===== 커뮤니티 ===== */
    COMMUNITY_LIST("커뮤니티 목록"),
    COMMUNITY_DETAIL("커뮤니티 상세"),
    COMMUNITY_ACTION("커뮤니티 조치"),

    /* ===== 고객센터 ===== */
    CS_LIST("고객센터 목록"),
    CS_DETAIL("고객센터 상세"),
    CS_ANSWER("고객센터 답변"),

    /* ===== 콘텐츠 ===== */
    CONTENT_LIST("콘텐츠 목록"),
    BANNER("배너"),
    NOTICE("공지사항"),
    FILE("파일 관리"),

    /* ===== 관리자 ===== */
    ADMIN_LIST("관리자 목록"),

    /* ===== 로그 ===== */
    LOG_LIST("로그 목록"),
    LOG_FILE("로그 파일");

    private final String description;
}
