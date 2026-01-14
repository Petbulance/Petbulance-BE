package com.example.Petbulance_BE.domain.admin.login.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdminLoginReqDto {

    private String username;

    private String password;

}
