package com.example.Petbulance_BE.domain.admin.hospital.dto;

import com.example.Petbulance_BE.domain.admin.hospital.entity.HospitalHistory;
import com.example.Petbulance_BE.domain.hospitalWorktime.entity.HospitalWorktime;
import jakarta.persistence.Column;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminHospitalDetailResDto {

    private String name;

    private String address;

    private String streetAddress;

    private String phoneNumber;

    private String information;

    private Double lat;

    private Double lng;

    private String url;

    private String image;

    private boolean nighCare;

    private boolean twentyFourHours;

    private String tag;

    private String treatmentAnimalType;

    private List<HospitalWorktimeResDto> worktimes;

    private List<HospitalHistoriesResDto> hospitalHistories;

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HospitalHistoriesResDto {

        private Long hospitalId;

        private String modifySubject;

        private String beforeModify;

        private String afterModify;

        private String actorId;

        private LocalDateTime createdAt;

    }

}
