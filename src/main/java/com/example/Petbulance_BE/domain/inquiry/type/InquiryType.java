package com.example.Petbulance_BE.domain.inquiry.type;

import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum InquiryType {
    ADVERTISING("광고문의"), HOSPITAL_PARTNERSHIP("병원제휴문의");

    private String typeKr;

    public static InquiryType fromString(String input) {
        if (input == null) throw new CustomException(ErrorCode.INVALID_TYPE);
        for (InquiryType type : InquiryType.values()) {
            // 한글 이름 또는 영어 enum 이름 둘 다 대응 가능
            if (type.typeKr.equals(input) || type.name().equalsIgnoreCase(input)) {
                return type;
            }
        }
        throw new CustomException(ErrorCode.INVALID_TYPE);
    }
}
