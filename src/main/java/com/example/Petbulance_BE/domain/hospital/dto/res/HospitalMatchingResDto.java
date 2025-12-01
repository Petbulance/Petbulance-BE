package com.example.Petbulance_BE.domain.hospital.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HospitalMatchingResDto {
    private Long hospitalId;
    private String thumbnailUrl;
    private String name;
    private boolean isOpenNow;
    private Double distanceKm;
    private LocalTime todayCloseTime;
    private List<String> treatableAnimals;
    private String phone;
    private List<String> tags;

    @Builder
    public HospitalMatchingResDto(
            Long hospitalId,
            String thumbnailUrl,
            String name,
            Boolean isOpenNow,
            Double distanceKm,
            LocalTime todayCloseTime,
            String phone
    ) {
        this.hospitalId = hospitalId;
        this.thumbnailUrl = thumbnailUrl;
        this.name = name;
        this.isOpenNow = isOpenNow;
        this.distanceKm = distanceKm;
        this.todayCloseTime = todayCloseTime;
        this.phone = phone;
    }
}