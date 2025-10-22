package com.example.Petbulance_BE.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileImageUpdateReqeustDto {

    @NotBlank(message = "파일명을 입력해주세요.")
    private String filename;

    private String contentType;
}
