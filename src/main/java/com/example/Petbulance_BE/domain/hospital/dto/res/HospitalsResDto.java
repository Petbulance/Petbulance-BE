package com.example.Petbulance_BE.domain.hospital.dto.res;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HospitalsResDto {

    private Long hospitalId;

    private String name;

    private Double lat;

    private Double lng;

    private Double distanceMeters;

    private String phone;

    private String[] types;

    private Boolean isOpenNow;

    private String openHours;

    private String thumbnailUrl;

    private Double rating;

    private Long reviewCount;

}
