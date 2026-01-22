package com.example.Petbulance_BE.domain.hospital.dto;

import com.example.Petbulance_BE.global.common.type.AnimalType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReviewSearchDto {

    private String userNickname;

    private Boolean receiptCheck;

    private Long id;

    private String hospitalImage;

    private Long hospitalId;

    private String hospitalName;

    private String treatmentService;

    private AnimalType animalType;

    private String detailAnimalType;

    private String reviewContent;

    private Double overallRating;

    private LocalDate createdDate;

    private List<String> images;

    private Long totalPrice;

    private Long likedCount;

    private Boolean liked;


}
