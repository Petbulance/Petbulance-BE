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
@Builder
public class DetailHospitalResDto {

    private Long hospitalId;
    private String name;

    private Double reviewAvg;
    private Integer reviewCount;

    private Boolean openNow;
    private LocalTime todayCloseTime;

    private Double distanceKm;

    private String phone;

    private List<String> acceptedAnimals;

    private Location location;
    private List<OpenHour> openHours;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Location {
        private String address;
        private Double lat;
        private Double lng;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OpenHour {
        private String day;      // ex) MON, TUE, WED
        private String hours;    // ex) "09:00-19:00", "휴진"
    }
}
