package com.example.Petbulance_BE.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SocialConnectRequestDto {

    private String provider;

    private String authCode;
}
