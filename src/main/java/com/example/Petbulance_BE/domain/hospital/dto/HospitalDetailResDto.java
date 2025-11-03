package com.example.Petbulance_BE.domain.hospital.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HospitalDetailResDto {

    private Long hospitalId;

    private String name;

    private String address;

    private Double lat;

    private Double lng;

    private String phone;

    private List<String> acceptedAnimals = new ArrayList<>();

    private List<OpenHours> openHours = new ArrayList<>();

    private String notes;

    private AtomicReference<Boolean> openNow;

    private String description;
}
