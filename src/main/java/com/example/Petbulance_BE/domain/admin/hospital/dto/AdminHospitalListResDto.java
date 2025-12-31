package com.example.Petbulance_BE.domain.admin.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class AdminHospitalListResDto {

    private long id;

    private String name;

}
