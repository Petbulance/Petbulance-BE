package com.example.Petbulance_BE.domain.review.dto.res;

import com.example.Petbulance_BE.global.common.type.AnimalType;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalUserReviewResDto {

    private Long id;

    private Boolean receiptChecked;

    private Boolean likedChecked;

    private Long likeCount;

    private String nickName;

    private LocalDate visitDate;

    private AnimalType animalType;

    private String detailAnimalType;

    private String treatment;

    private Double totalRating;

    private String comment;

    private List<String> image;

}
