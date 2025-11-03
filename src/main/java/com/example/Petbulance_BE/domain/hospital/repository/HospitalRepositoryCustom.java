package com.example.Petbulance_BE.domain.hospital.repository;

import com.example.Petbulance_BE.domain.hospital.dto.HospitalSearchReqDto;
import com.example.Petbulance_BE.domain.hospital.dto.HospitalSearchRes;
import com.example.Petbulance_BE.domain.hospital.dto.HospitalsResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface HospitalRepositoryCustom {
    Page<HospitalSearchRes> searchHospitals(HospitalSearchReqDto dto, Pageable pageable);
}
