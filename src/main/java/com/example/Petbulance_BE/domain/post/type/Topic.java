package com.example.Petbulance_BE.domain.post.type;

import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum Topic {

    HEALTH("건강/질병"),
    SUPPLIES("용품/사료"),
    DAILY("일상/자랑"),
    TRADE("중고거래");

    private final String description;

    public static List<Topic> convertToCategoryList(List<String> categoryStrings) {
        if (categoryStrings == null || categoryStrings.isEmpty()) {
            return Collections.emptyList();
        }

        List<Topic> categories = new ArrayList<>();
        for (String cat : categoryStrings) {
            try {
                categories.add(Topic.valueOf(cat.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new CustomException(ErrorCode.INVALID_CATEGORY);
            }
        }
        return categories;
    }

    public static boolean isValidCategory(String category) {
        try {
            Topic.valueOf(category.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_CATEGORY);
        }
    }

    public static Topic fromString(String categoryString) {
        if (categoryString == null || categoryString.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_BOARD_OR_CATEGORY);
        }
        try {
            return Topic.valueOf(categoryString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_BOARD_OR_CATEGORY);
        }
    }

}
