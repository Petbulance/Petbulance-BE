package com.example.Petbulance_BE.global.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AnimalType {
    SMALLMAMMALS("소형 포유류"),
    BIRDS("조류"),
    REPTILES("파충류"),
    AMPHIBIANS("양서류"),
    FISH("어류");

    private final String description;
}
