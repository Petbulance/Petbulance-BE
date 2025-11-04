package com.example.Petbulance_BE.domain.inquiry.type;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum InquiryType {
    ADVERTISING("광고문의"), HOSPITAL_PARTNERSHIP("병원제휴문의");

    private String typeKr;

    public static InquiryType fromString(String input) {
        if (input == null) return null;
        for (InquiryType type : InquiryType.values()) {
            // 한글 이름 또는 영어 enum 이름 둘 다 대응 가능
            if (type.typeKr.equals(input) || type.name().equalsIgnoreCase(input)) {
                return type;
            }
        }
        return null; // 매칭 안 될 경우 null 반환
    }
}
