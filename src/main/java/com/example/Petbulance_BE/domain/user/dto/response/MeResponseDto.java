package com.example.Petbulance_BE.domain.user.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeResponseDto {

    private String provider;

    private String email;

    private String nickname;

    private String profileImageUrl;

    private String kakaoEmail;

    private String googleEmail;

    private String naverEmail;
}
