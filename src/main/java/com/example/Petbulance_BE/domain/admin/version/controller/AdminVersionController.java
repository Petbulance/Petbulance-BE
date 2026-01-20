package com.example.Petbulance_BE.domain.admin.version.controller;

import com.example.Petbulance_BE.domain.admin.version.dto.*;
import com.example.Petbulance_BE.domain.admin.version.service.AdminVersionService;
import com.example.Petbulance_BE.domain.admin.version.type.RegionType;
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

    @GetMapping("/region")
    public RegionResDto getRegion() {

        return adminVersionService.getRegionProcess();

    }

    @GetMapping("/species")
    public List<SpeciesResDto> getSpecies() {

        return adminVersionService.getSpeciesProcess();

    }

    @GetMapping("/category")
    public List<CategoryResDto> getCategory() {

        return adminVersionService.getCategoryProcess();

    }

    @PostMapping("/terms")
    public Map<String, String> postTerms(@RequestBody @Valid TermsReqDto termsReqDto){

        return adminVersionService.postTermsProcess(termsReqDto);

    }


}
