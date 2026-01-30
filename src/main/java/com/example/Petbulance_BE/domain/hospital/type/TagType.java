package com.example.Petbulance_BE.domain.hospital.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TagType {

    WORKTYPE("운영정보"), ANIMALTYPE("동물종정보"), LOCATIONTYPE("위치정보");

    private final String description;

}
