package com.example.Petbulance_BE.domain.terms.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TermsStatus {

    AGREE("동의"), DISAGREE("비동의"), EXPIRED("구버전");

    private final String description;

}
