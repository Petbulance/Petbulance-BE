package com.example.Petbulance_BE.domain.adminlog.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdminActionType {

    /* ===== 인증 / 세션 ===== */
    LOGIN("로그인"),
    LOGOUT("로그아웃"),
    SESSION_CHECK("세션 확인"),

    /* ===== 조회 ===== */
    READ("조회"),
    SEARCH("검색"),

    /* ===== 생성 ===== */
    CREATE("생성"),
    UPLOAD("파일 업로드"),

    /* ===== 수정 ===== */
    UPDATE("수정"),

    /* ===== 삭제 ===== */
    DELETE("삭제"),

    /* ===== 기타 ===== */
    EXPORT("엑셀/CSV 내보내기"),
    ACCESS("접근 시도"),

    /* ===== 실패 / 에러 ===== */
    FAIL("처리 실패"),
    ERROR("시스템 오류");

    private final String description;
}
