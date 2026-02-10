package com.example.Petbulance_BE.global.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AnimalType {
    SMALLMAMMALS("소형포유류"),
    AVIAN("조류"),
    REPTILE("파충류"),
    AMPHIBIAN("양서류"),
    FISH("어류");


    private final String description;
}
