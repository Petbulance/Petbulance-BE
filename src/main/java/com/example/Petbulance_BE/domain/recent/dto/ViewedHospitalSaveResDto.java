package com.example.Petbulance_BE.domain.recent.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ViewedHospitalSaveResDto {

    private Long id;

    private LocalDateTime viewedAt;
}
