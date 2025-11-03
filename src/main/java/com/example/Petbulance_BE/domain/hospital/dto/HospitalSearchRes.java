package com.example.Petbulance_BE.domain.hospital.dto;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HospitalSearchRes {

    private Long id;
    private String name;
    private Double lat;
    private Double lng;
    private String phoneNumber;
    private String url;
    private Double distanceMeters;
    private String treatedAnimalTypes;
    private Long reviewCount;
    private Double rating;

    // 요일별 영업시간
    private LocalTime monOpenTime;
    private LocalTime monCloseTime;
    private LocalTime monBreakStartTime;
    private LocalTime monBreakEndTime;
    private LocalTime monReceptionDeadline;
    private Boolean monIsOpen;

    private LocalTime tueOpenTime;
    private LocalTime tueCloseTime;
    private LocalTime tueBreakStartTime;
    private LocalTime tueBreakEndTime;
    private LocalTime tueReceptionDeadline;
    private Boolean tueIsOpen;

    private LocalTime wedOpenTime;
    private LocalTime wedCloseTime;
    private LocalTime wedBreakStartTime;
    private LocalTime wedBreakEndTime;
    private LocalTime wedReceptionDeadline;
    private Boolean wedIsOpen;

    private LocalTime thuOpenTime;
    private LocalTime thuCloseTime;
    private LocalTime thuBreakStartTime;
    private LocalTime thuBreakEndTime;
    private LocalTime thuReceptionDeadline;
    private Boolean thuIsOpen;

    private LocalTime friOpenTime;
    private LocalTime friCloseTime;
    private LocalTime friBreakStartTime;
    private LocalTime friBreakEndTime;
    private LocalTime friReceptionDeadline;
    private Boolean friIsOpen;

    private LocalTime satOpenTime;
    private LocalTime satCloseTime;
    private LocalTime satBreakStartTime;
    private LocalTime satBreakEndTime;
    private LocalTime satReceptionDeadline;
    private Boolean satIsOpen;

    private LocalTime sunOpenTime;
    private LocalTime sunCloseTime;
    private LocalTime sunBreakStartTime;
    private LocalTime sunBreakEndTime;
    private LocalTime sunReceptionDeadline;
    private Boolean sunIsOpen;
}
