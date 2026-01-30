package com.example.Petbulance_BE.domain.admin.login.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminRegisterReqDto {

    private String nickname;

    private String password;

}
