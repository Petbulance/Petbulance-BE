package com.example.Petbulance_BE.domain.admin.version.controller;

import com.example.Petbulance_BE.domain.admin.version.dto.*;
import com.example.Petbulance_BE.domain.admin.version.service.AdminVersionService;
import com.example.Petbulance_BE.domain.admin.version.type.RegionType;
import com.example.Petbulance_BE.domain.adminlog.aop.AdminLoggable;
import com.example.Petbulance_BE.domain.adminlog.type.AdminActionType;
import com.example.Petbulance_BE.domain.adminlog.type.AdminPageType;
import com.example.Petbulance_BE.domain.adminlog.type.AdminTargetType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/version")
public class AdminVersionController {

    private final AdminVersionService adminVersionService;

    @AdminLoggable(
            pageType = AdminPageType.ACTION_LOG,
            actionType = AdminActionType.READ,
            targetType = AdminTargetType.SYSTEM,
            description = "지역 정보 조회"
    )
    @GetMapping("/region")
    public RegionResDto getRegion() {

        return adminVersionService.getRegionProcess();

    }

    @AdminLoggable(
            pageType = AdminPageType.ACTION_LOG,
            actionType = AdminActionType.READ,
            targetType = AdminTargetType.SYSTEM,
            description = "동물종 정보 조회"
    )
    @GetMapping("/species")
    public List<SpeciesResDto> getSpecies() {

        return adminVersionService.getSpeciesProcess();

    }

    @AdminLoggable(
            pageType = AdminPageType.ACTION_LOG,
            actionType = AdminActionType.READ,
            targetType = AdminTargetType.SYSTEM,
            description = "카테고리 정보 조회"
    )
    @GetMapping("/category")
    public List<CategoryResDto> getCategory() {

        return adminVersionService.getCategoryProcess();

    }

    @AdminLoggable(
            pageType = AdminPageType.ACTION_LOG,
            actionType = AdminActionType.CREATE,
            targetType = AdminTargetType.SYSTEM,
            targetId = "#termsReqDto.version",
            description = "약관 생성(타겟 아이디는 생성 약관 버전명)"
    )
    @PostMapping("/terms")
    public Map<String, String> postTerms(@RequestBody @Valid TermsReqDto termsReqDto){

        return adminVersionService.postTermsProcess(termsReqDto);

    }


}
