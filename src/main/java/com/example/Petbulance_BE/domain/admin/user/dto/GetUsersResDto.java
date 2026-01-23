package com.example.Petbulance_BE.domain.admin.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetUsersResDto {

    private String userId;

    private String nickname;

    private String email;

    private String signUpPath;

    private Integer warnings;

    private Boolean reviewBan;

    private Boolean communityBan;

    private LocalDateTime createdAt;

}
