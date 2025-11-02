package com.example.Petbulance_BE.domain.qna.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateQnaReqDto {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private String password;
}
