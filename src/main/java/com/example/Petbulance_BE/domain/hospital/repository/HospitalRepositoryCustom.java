package com.example.Petbulance_BE.domain.hospital.repository;

import com.example.Petbulance_BE.domain.hospital.dto.req.HospitalSearchReqDto;
import com.example.Petbulance_BE.domain.hospital.dto.HospitalSearchDao;
import com.example.Petbulance_BE.domain.hospital.dto.res.DetailHospitalResDto;
import com.example.Petbulance_BE.domain.hospital.dto.res.HospitalMatchingResDto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;


public interface HospitalRepositoryCustom {
    List<HospitalSearchDao> searchHospitals(HospitalSearchReqDto dto);
    List<HospitalMatchingResDto> findMatchingHospitals(
            String species,
            String filter,
            Double lat,
            Double lng,
            DayOfWeek today,
            LocalTime now
    );
    DetailHospitalResDto findHospitalDetail(Long hospitalId, Double lat, Double lng);
}
