package com.example.Petbulance_BE.domain.qna.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateQnaReqDto {
    @NotBlank(message = "제목(title)은 비워둘 수 없습니다.")
    private String title;

    @NotBlank(message = "내용(content)은 비워둘 수 없습니다.")
    private String content;

    @Pattern(regexp = "^[0-9]{4}$", message = "비밀번호(password)는 4자리 숫자여야 합니다.")
    private String password;
}
