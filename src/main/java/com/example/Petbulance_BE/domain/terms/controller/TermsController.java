package com.example.Petbulance_BE.domain.terms.controller;

import com.example.Petbulance_BE.domain.terms.dto.res.TermsStatusResDto;
import com.example.Petbulance_BE.domain.terms.enums.TermsType;
import com.example.Petbulance_BE.domain.terms.dto.req.ConsentsReqDto;
import com.example.Petbulance_BE.domain.terms.dto.res.GetTermsResDto;
import com.example.Petbulance_BE.domain.terms.service.TermsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/terms")
public class TermsController {

    private final TermsService termsService;

    @GetMapping
    public List<GetTermsResDto> getTerms() {
        return termsService.getTermsProcess();
    }

    @GetMapping("/{type}")
    public GetTermsResDto getOneTerms(@PathVariable TermsType type) {
        return termsService.getOneTermsProcess(type);
    }

    @PostMapping("/consents")
    public Map<String, String> consents(@RequestBody @Valid ConsentsReqDto consentsReqDto){
        return termsService.consentsProcess(consentsReqDto);
    }

    @GetMapping("/status")
    public TermsStatusResDto status(){
        return termsService.statusProcess();
    }

    @DeleteMapping("/{type}")
    public Map<String, String> deleteOneTerms(@PathVariable TermsType type) {
        return termsService.deleteOneTermsProcess(type);
    }

}
