package com.example.Petbulance_BE.global.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    ROLE_CLIENT("유저"),
    ROLE_ADMIN("관리자");

    private final String description;

}
