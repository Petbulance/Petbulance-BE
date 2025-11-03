package com.example.Petbulance_BE.domain.hospital.controller;

import com.example.Petbulance_BE.domain.hospital.dto.*;
import com.example.Petbulance_BE.domain.hospital.service.HospitalService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    @GetMapping
    public Page<HospitalsResDto> searchHospitals(@ModelAttribute HospitalSearchReqDto hospitalSearchReqDto, Pageable pageable) {
        return hospitalService.searchHospitalsProcess(hospitalSearchReqDto, pageable);
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
