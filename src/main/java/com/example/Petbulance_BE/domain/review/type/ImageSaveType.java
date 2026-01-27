package com.example.Petbulance_BE.domain.review.type;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ImageSaveType {
    NEW("저장"), UPDATE("수정");

    private final String description;
}
