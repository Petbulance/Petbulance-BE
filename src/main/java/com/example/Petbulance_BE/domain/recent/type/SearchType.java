package com.example.Petbulance_BE.domain.recent.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchType {
    SEARCHHOSPITAL("최근 검색한 병원"),
    WATCHHOSPITAL("최근 본 병원");

    private final String description;


}
