package com.example.Petbulance_BE.domain.review.dto.req;

import com.example.Petbulance_BE.global.common.type.AnimalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilterReqDto {

    private String region;

    private AnimalType animalType;

    private Boolean receipt;

    private Long cursorId;

    private int size = 10;
}
