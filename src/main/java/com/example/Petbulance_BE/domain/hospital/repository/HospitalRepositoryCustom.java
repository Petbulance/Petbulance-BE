package com.example.Petbulance_BE.domain.hospital.repository;

import com.example.Petbulance_BE.domain.hospital.dto.req.HospitalSearchReqDto;
import com.example.Petbulance_BE.domain.hospital.dto.HospitalSearchDao;

import java.util.List;


public interface HospitalRepositoryCustom {
    List<HospitalSearchDao> searchHospitals(HospitalSearchReqDto dto);
}
