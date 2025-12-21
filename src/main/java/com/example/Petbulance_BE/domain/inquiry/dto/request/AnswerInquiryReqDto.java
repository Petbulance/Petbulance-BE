package com.example.Petbulance_BE.domain.inquiry.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerInquiryReqDto {
    @NotBlank(message = "게시글 내용(content)은 필수입니다.")
    private String content;
}
