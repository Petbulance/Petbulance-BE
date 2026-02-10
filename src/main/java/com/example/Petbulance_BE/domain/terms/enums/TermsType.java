package com.example.Petbulance_BE.domain.terms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TermsType {
    SERVICE(1, "서비스 이용약관"),
    PRIVACY(2, "개인정보 처리방침"),
    LOCATION(3, "위치기반 서비스 이용약관"),
    MARKETING(4, "마케팅 정보 수신 동의약관");

    private final int sort;

    private final String description;

}
