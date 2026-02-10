package com.example.Petbulance_BE.domain.admin.user.dto;

import com.example.Petbulance_BE.global.common.type.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetUserQueryParam {

    private String usernameOrEmail;

    private String signUpPath;

    private String userStatus;

    private Role userType;

}
