package com.example.Petbulance_BE.domain.qna.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateQnaReqDto {
    @NotBlank(message = "제목(title)은 비워둘 수 없습니다.")
    private String title;

    @NotBlank(message = "내용(content)은 비워둘 수 없습니다.")
    private String content;
}
