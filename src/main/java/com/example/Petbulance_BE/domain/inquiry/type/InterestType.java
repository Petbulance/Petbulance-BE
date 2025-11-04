package com.example.Petbulance_BE.domain.inquiry.type;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum InterestType {
    BANNER_AD("배너광고"),
    EVENT_COLLABORATION("이벤트 협업"),
    HOSPITAL_REGISTRATION("병원 등록"),
    ETC("기타");

    private String typeKr;

    public static InterestType fromString(String input) {
        if (input == null) return null;
        for (InterestType type : InterestType.values()) {
            if (type.typeKr.equals(input) || type.name().equalsIgnoreCase(input)) {
                return type;
            }
        }
        return null;
    }
}
