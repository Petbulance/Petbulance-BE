package com.example.Petbulance_BE.domain.review.dto.req;

import com.example.Petbulance_BE.global.common.type.AnimalType;
import com.example.Petbulance_BE.global.common.type.DetailAnimalType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewModifyReqDto {

    private Long reviewId;

    private String title;

    private Boolean receiptChecked;

    @NotNull
    private Long hospitalId;

    @NotNull
    private Double expertiseRating;

    @NotNull
    private Double kindnessRating;

    @NotNull
    private Double facilityRating;

    @NotNull
    private Long totalPrice;

    @NotNull
    private AnimalType animalType;

    @NotBlank
    private DetailAnimalType DetailAnimalType;

    @NotNull
    private List<ReviewSaveReqDto.ReceiptItem> receiptItems = new ArrayList<>();

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate visitDate;

    @NotBlank
    private String reviewComment;

    private List<ReviewSaveReqDto.Images> images  = new ArrayList<>();

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Images{

        private String filename;

        private String contentType;
    }

    @Getter
    @Setter
    public static class ReceiptItem {
        private String name;
        private Integer price;
    }
}
