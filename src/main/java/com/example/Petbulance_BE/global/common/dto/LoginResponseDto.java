package com.example.Petbulance_BE.global.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {

    private Boolean isNewUser;

    private String firebaseCustomToken;

    private String accessToken;

    private String refreshToken;
}
