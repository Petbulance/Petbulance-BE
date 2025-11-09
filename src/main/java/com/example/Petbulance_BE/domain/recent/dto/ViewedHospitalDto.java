package com.example.Petbulance_BE.domain.recent.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewedHospitalDto {

    private Long hospitalId;

    private String name;

    private LocalDateTime viewedAt;

}
