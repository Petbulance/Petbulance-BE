package com.example.Petbulance_BE.domain.review.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FindHospitalResDto {

    List<HospitalDto> hospitals;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HospitalDto {

        Long hospitalId;

        String hospitalName;

    }
}
