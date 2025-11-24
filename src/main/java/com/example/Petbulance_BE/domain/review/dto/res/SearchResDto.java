package com.example.Petbulance_BE.domain.review.dto.res;

import com.example.Petbulance_BE.global.common.type.AnimalType;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResDto {

    private Boolean receiptCheck;

    private Long id;

    private String treatmentService;

    private AnimalType animalType;

    private String detailAnimalType;

    private String reviewContent;

    private Double totalRating;

    private LocalDate reviewDate;

    private int likeCount;

    private Boolean liked;

    @Builder.Default
    private List<String> images = new ArrayList<>();

}
