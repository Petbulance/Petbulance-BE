package com.example.Petbulance_BE.domain.hospital.controller;

import com.example.Petbulance_BE.domain.hospital.dto.req.HospitalSearchReqDto;
import com.example.Petbulance_BE.domain.hospital.dto.res.HospitalCardResDto;
import com.example.Petbulance_BE.domain.hospital.dto.res.HospitalDetailResDto;
import com.example.Petbulance_BE.domain.hospital.dto.res.HospitalSearchResDto;
import com.example.Petbulance_BE.domain.hospital.dto.res.HospitalsResDto;
import com.example.Petbulance_BE.domain.hospital.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    @GetMapping
    public HospitalSearchResDto searchHospitals(@ModelAttribute HospitalSearchReqDto hospitalSearchReqDto) {
        return hospitalService.searchHospitalsProcess(hospitalSearchReqDto);
    }

    @GetMapping("/{hospitalId}")
    public HospitalDetailResDto searchHospitalDetail(@PathVariable Long hospitalId){
        return hospitalService.searchHospitalDetailProcess(hospitalId);
    }

    @GetMapping("/card/{hospitalId}")
    public HospitalCardResDto searchHospitalCard(@PathVariable Long hospitalId, @RequestParam Double lat, @RequestParam Double lng){
        return hospitalService.searchHospitalCardProcess(hospitalId , lat, lng);
    }
}
