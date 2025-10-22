package com.example.Petbulance_BE.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NicknameResponseDto {

    private String nickname;

    private Boolean available;

    private String reason;
}
