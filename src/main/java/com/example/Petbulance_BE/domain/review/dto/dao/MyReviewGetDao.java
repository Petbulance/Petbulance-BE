package com.example.Petbulance_BE.domain.review.dto.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class MyReviewGetDao {

    public MyReviewGetDao(Long id, String hospitalName, String hospitalImage,
                          LocalDateTime createdAt, Boolean receiptCheck,
                          Integer likeCount, String reviewContent) {
        this.id = id;
        this.hospitalName = hospitalName;
        this.hospitalImageUrl = hospitalImage;
        this.reviewDate = createdAt.toLocalDate(); // 여기서 변환
        this.receiptChecked = receiptCheck;
        this.likeCount = likeCount;
        this.comment = reviewContent;
    }

    private Long id;

    private String hospitalName;

    private String hospitalImageUrl;

    private LocalDate reviewDate;

    private Boolean receiptChecked;

    private Integer likeCount;

    private String comment;

}
