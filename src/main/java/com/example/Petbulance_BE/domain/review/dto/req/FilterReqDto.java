package com.example.Petbulance_BE.domain.review.dto.req;

import com.example.Petbulance_BE.global.common.type.AnimalType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterReqDto {

    private String region;

    private List<AnimalType> animalType;

    private Boolean receipt;

    private Long cursorId;

    private Double cursorRating;

    private Long cursorLikes;

    private int size = 10;

    private String sort; //null, rating, likeCount

    @Builder.Default
    private Boolean images = false; //true, false
}
