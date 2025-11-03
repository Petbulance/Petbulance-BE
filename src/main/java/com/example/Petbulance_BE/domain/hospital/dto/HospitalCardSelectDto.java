package com.example.Petbulance_BE.domain.hospital.dto;

import com.example.Petbulance_BE.domain.hospital.entity.Hospital;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class HospitalCardSelectDto {
    private Hospital hospital;
    private Double distance;

    public HospitalCardSelectDto(Hospital hospital, Double distance) {
        this.hospital = hospital;
        this.distance = distance;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public Double getDistance() {
        return distance;
    }
}