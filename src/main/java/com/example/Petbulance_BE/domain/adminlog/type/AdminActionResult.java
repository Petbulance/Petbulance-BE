package com.example.Petbulance_BE.domain.adminlog.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdminActionResult {
    SUCCESS("성공"),
    FAIL("실패");

    private final String description;

}