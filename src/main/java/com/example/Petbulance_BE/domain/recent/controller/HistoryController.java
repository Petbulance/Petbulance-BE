package com.example.Petbulance_BE.domain.recent.controller;

import com.example.Petbulance_BE.domain.hospital.service.HospitalService;
import com.example.Petbulance_BE.domain.recent.dto.*;
import com.example.Petbulance_BE.domain.recent.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recents")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;
    private final HospitalService hospitalService;

    @GetMapping("/hospitals")
    public List<RecentHospitalResDto> recentHospitals() {
        return historyService.recentHospitalsProcess();
    }

    @PostMapping("/hospitals")
    public RecentHospitalSaveResDto saveRecentHospital(@RequestBody RecentHospitalSaveReqDto recentHospitalSaveReqDto) {
        return historyService.recentHospitalSaveProcess(recentHospitalSaveReqDto.getKeyword());
    }

    @DeleteMapping("/hospitals/{keywordId}")
    public void deleteRecentHospital(@PathVariable Long keywordId) {
        historyService.recentHospitalDeleteProcess(keywordId);
    }

    @PostMapping("/viewed")
    public ViewedHospitalSaveResDto viewedHospitalSave(@RequestBody ViewedHospitalSaveReqDto saveReqDto) {
        return historyService.viewedHospitalSaveProcess(saveReqDto);
    }

    @GetMapping("/viewed")
    public ViewedHospitalResDto viewedHospital(){
        List<ViewedHospitalDto> list = historyService.viewedHospitalProcess();
        return new ViewedHospitalResDto(list, list.stream().count());
    }

    @DeleteMapping("/viewed/{hospitalId}")
    public void viewedHospitalDelete(@PathVariable Long hospitalId) {
        historyService.viewedHospitalDeleteProcess(hospitalId);
    }
}
