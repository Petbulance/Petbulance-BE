package com.example.Petbulance_BE.domain.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReviewSearchDto {

    private Boolean receiptCheck;

    private Long id;

    private String hospitalImage;

    private Long hospitalId;

    private String hospitalName;

    private String treatmentService;

    private String detailAnimalType;

    private String reviewContent;

    private Double overallRating;

}
