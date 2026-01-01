package com.example.Petbulance_BE.domain.admin.hospital.dto;

import com.example.Petbulance_BE.domain.hospitalWorktime.entity.HospitalWorktime;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class HospitalWorktimeResDto {
    private String dayOfWeek;
    private LocalTime openTime;
    private LocalTime closeTime;
    private LocalTime breakStartTime;
    private LocalTime breakEndTime;
    private LocalTime receptionDeadline;
    private Boolean isOpen;

    public HospitalWorktimeResDto(HospitalWorktime entity) {
        this.dayOfWeek = entity.getId().getDayOfWeek();

        this.openTime = entity.getOpenTime();
        this.closeTime = entity.getCloseTime();
        this.breakStartTime = entity.getBreakStartTime();
        this.breakEndTime = entity.getBreakEndTime();
        this.receptionDeadline = entity.getReceptionDeadline();
        this.isOpen = entity.getIsOpen();

    }
}