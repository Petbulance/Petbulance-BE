package com.example.Petbulance_BE.domain.recent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Builder
@AllArgsConstructor
@Getter
public class RecentHospitalResDto {

    private Long keywordId;

    private String keyword;

    private LocalDateTime createdAt;

}
