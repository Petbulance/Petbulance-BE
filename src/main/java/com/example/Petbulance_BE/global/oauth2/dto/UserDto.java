package com.example.Petbulance_BE.global.oauth2.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String userId;
    private String provider;
    private String email;
    private String role;
}
