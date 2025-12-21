package com.example.Petbulance_BE.domain.inquiry.dto.request;

import com.example.Petbulance_BE.domain.inquiry.type.InquiryType;
import com.example.Petbulance_BE.domain.inquiry.type.InterestType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateInquiryReqDto {

    @NotNull(message = "문의 유형(type)은 필수입니다.")
    private InquiryType type;

    @NotBlank(message = "회사명(companyName)은 비워둘 수 없습니다.")
    private String companyName;

    @NotBlank(message = "담당자명(managerName)은 비워둘 수 없습니다.")
    private String managerName;

    private String managerPosition;

    @Pattern(
            regexp = "^010-\\d{4}-\\d{4}$",
            message = "전화번호는 010-0000-0000 형식이어야 합니다."
    )
    private String phone;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotNull(message = "관심유형(interestType)은 필수입니다.")
    private InterestType interestType;

    @NotBlank(message = "문의 내용(content)은 비워둘 수 없습니다.")
    private String content;

    @AssertTrue(message = "개인정보 수집 및 이용에 동의해야 합니다.")
    private boolean privacyConsent;
}
