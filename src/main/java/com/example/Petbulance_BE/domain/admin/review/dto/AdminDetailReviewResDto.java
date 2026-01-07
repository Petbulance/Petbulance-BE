package com.example.Petbulance_BE.domain.admin.review.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDetailReviewResDto {

    private String hospitalName;

    private String address;

    private String roadAddress;

    private Double latitude;

    private Double longitude;

    private String treatmentService;

    private List<String> images;

    private String title;

    private Long totalPrice;

    private Double facilityRating;

    private Double expertiseRating;

    private Double kindnessRating;

    private String reviewContent;

}
