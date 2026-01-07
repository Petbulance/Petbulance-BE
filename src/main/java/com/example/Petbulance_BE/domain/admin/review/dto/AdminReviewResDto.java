package com.example.Petbulance_BE.domain.admin.review.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AdminReviewResDto {

    private long reviewId;

    private String hospitalName;

    private String userNickName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewDate;

    private String reviewStatus; //hidden, deleted

    private Integer reportedCount;

}
