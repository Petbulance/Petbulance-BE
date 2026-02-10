package com.example.Petbulance_BE.domain.admin.hospital.controller;

import com.example.Petbulance_BE.domain.admin.hospital.dto.AdminHospitalDetailResDto;
import com.example.Petbulance_BE.domain.admin.hospital.dto.AdminHospitalListResDto;
import com.example.Petbulance_BE.domain.admin.hospital.dto.AdminSaveHospitalReqDto;
import com.example.Petbulance_BE.domain.admin.page.PageResponse;
import com.example.Petbulance_BE.domain.admin.hospital.service.AdminHospitalService;
import com.example.Petbulance_BE.domain.adminlog.aop.AdminLoggable;
import com.example.Petbulance_BE.domain.adminlog.type.AdminActionType;
import com.example.Petbulance_BE.domain.adminlog.type.AdminPageType;
import com.example.Petbulance_BE.domain.adminlog.type.AdminTargetType;
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

    @AdminLoggable(
            pageType = AdminPageType.HOSPITAL_MANAGEMENT,
            actionType = AdminActionType.CREATE,
            targetType = AdminTargetType.HOSPITAL_DETAIL,
            targetId = "#adminSaveHospitalReqDto.hospitalName",
            description = "병원 생성"
    )
    @PostMapping("/save")
    public Long saveHospital(@RequestBody AdminSaveHospitalReqDto adminSaveHospitalReqDto){
        return adminHospitalService.saveHospitalProcess(adminSaveHospitalReqDto);
    }

    @AdminLoggable(
            pageType = AdminPageType.HOSPITAL_MANAGEMENT,
            actionType = AdminActionType.UPDATE,
            targetType = AdminTargetType.HOSPITAL_DETAIL,
            targetId = "#id",
            description = "병원 정보 수정"
    )
    @PutMapping("/update/{id}")
    public Long updateHospital(@RequestBody AdminSaveHospitalReqDto adminSaveHospitalReqDto, @PathVariable Long id) {
        return adminHospitalService.updateHospitalProcess(id, adminSaveHospitalReqDto);
    }

    @AdminLoggable(
            pageType = AdminPageType.HOSPITAL_MANAGEMENT,
            actionType = AdminActionType.DELETE,
            targetType = AdminTargetType.HOSPITAL_DETAIL,
            targetId = "#id",
            description = "병원 삭제"
    )
    @DeleteMapping("/delete/{id}")
    public boolean deleteHospital(@PathVariable Long id) {
        return adminHospitalService.deleteHospitalProcess(id);
    }

}
