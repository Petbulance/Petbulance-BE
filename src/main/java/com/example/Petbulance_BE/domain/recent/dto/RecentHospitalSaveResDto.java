package com.example.Petbulance_BE.domain.recent.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecentHospitalSaveResDto {

    Long keywordId;

    String keyword;

    LocalDateTime createdAt;
}
