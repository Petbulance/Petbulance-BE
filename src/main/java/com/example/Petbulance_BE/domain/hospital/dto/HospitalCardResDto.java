package com.example.Petbulance_BE.domain.hospital.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HospitalCardResDto {

    private Long hospitalId;

    private String name;

    private Double lat;

    private Double lng;

    private Double distanceMeters;

    private String phone;

    private List<String> types = new LinkedList<>();

    private Boolean isOpenNow;

    private String nextOpenHours;

    private String thumbnailUrl;

    private Double rating;

    private Long reviewCount;



}
