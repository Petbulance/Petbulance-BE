package com.example.Petbulance_BE.domain.recent.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViewedHospitalResDto {

    private List<ViewedHospitalDto> viewedHospitals;

    private Long total;
}
