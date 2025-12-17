package com.example.Petbulance_BE.domain.review.dto.res;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterResDto {

    private Boolean receiptCheck;

    private Long id;

    private String hospitalImage;

    private Long hospitalId;

    private String hospitalName;

    private String treatmentService;

    private String detailAnimalType;

    private String reviewContent;

    private Double totalRating;

    private Long totalReviewCount;

}
