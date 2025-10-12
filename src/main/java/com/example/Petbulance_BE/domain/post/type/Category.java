package com.example.Petbulance_BE.domain.post.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {

    HEALTH("건강/질병"),
    SUPPLIES("용품/사료"),
    DAILY("일상/자랑"),
    TRADE("중고거래");

    private final String description;
}
