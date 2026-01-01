package com.example.Petbulance_BE.domain.admin.hospital.controller;

import com.example.Petbulance_BE.domain.admin.hospital.dto.AdminHospitalDetailResDto;
import com.example.Petbulance_BE.domain.admin.hospital.dto.AdminHospitalListResDto;
import com.example.Petbulance_BE.domain.admin.hospital.dto.AdminSaveHospitalReqDto;
import com.example.Petbulance_BE.domain.admin.hospital.dto.page.PageResponse;
import com.example.Petbulance_BE.domain.admin.hospital.service.AdminHospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/hospital")
@RequiredArgsConstructor
public class AdminHospitalController {

    private final AdminHospitalService adminHospitalService;

    @GetMapping
    public PageResponse<AdminHospitalListResDto> findHospital (@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return adminHospitalService.findHospitalProcess(pageable);
    }

    @GetMapping("/{name}")
    public PageResponse<AdminHospitalListResDto> findNameHospital(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable, @PathVariable String name) {
        return adminHospitalService.findNameHospitalProcess(pageable, name);
    }

    @GetMapping("/detail/{id}")
    public AdminHospitalDetailResDto detailHospital(@PathVariable Long id) {
        return adminHospitalService.detailHospitalProcess(id);
    }

    @PostMapping("/save")
    public Long saveHospital(@RequestBody AdminSaveHospitalReqDto adminSaveHospitalReqDto){
        return adminHospitalService.saveHospitalProcess(adminSaveHospitalReqDto);
    }

    @PutMapping("/update/{id}")
    public Long updateHospital(@RequestBody AdminSaveHospitalReqDto adminSaveHospitalReqDto, @PathVariable Long id) {
        return adminHospitalService.updateHospitalProcess(id, adminSaveHospitalReqDto);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteHospital(@PathVariable Long id) {
        return adminHospitalService.deleteHospitalProcess(id);
    }

}
