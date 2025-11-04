package com.example.Petbulance_BE.domain.inquiry.controller;

import com.example.Petbulance_BE.domain.inquiry.dto.request.CreateInquiryReqDto;
import com.example.Petbulance_BE.domain.inquiry.dto.response.CreateInquiryResDto;
import com.example.Petbulance_BE.domain.inquiry.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inquiries")
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;

    @PostMapping
    public CreateInquiryResDto createInquiry(@RequestBody CreateInquiryReqDto dto) {
        return inquiryService.createInquiry(dto);
    }
}
