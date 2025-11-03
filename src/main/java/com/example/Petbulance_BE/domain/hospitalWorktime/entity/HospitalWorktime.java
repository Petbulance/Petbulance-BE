package com.example.Petbulance_BE.domain.hospitalWorktime.entity;

import com.example.Petbulance_BE.domain.hospital.entity.Hospital;
import com.example.Petbulance_BE.global.common.mapped.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalTime;

@Entity
@Getter
@Table(name = "hospitalworktimes")
public class HospitalWorktime extends BaseTimeEntity {

    @EmbeddedId
    private HospitalWorktimeKey id;

    @MapsId("hospitalId")
    @JoinColumn(name = "hospital_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Hospital hospital;

    private LocalTime openTime;

    private LocalTime closeTime;

    private LocalTime breakStartTime;

    private LocalTime breakEndTime;

    private LocalTime receptionDeadline;

    private Boolean isOpen;

}
