package com.example.Petbulance_BE.domain.hospitalWorktime.entity;

import com.example.Petbulance_BE.domain.hospital.entity.Hospital;
import com.example.Petbulance_BE.global.common.mapped.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hospital_worktimes")
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
