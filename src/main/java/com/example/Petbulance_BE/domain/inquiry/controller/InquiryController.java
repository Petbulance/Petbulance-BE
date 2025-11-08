package com.example.Petbulance_BE.domain.inquiry.controller;

import com.example.Petbulance_BE.domain.inquiry.dto.request.CreateInquiryReqDto;
import com.example.Petbulance_BE.domain.inquiry.dto.request.UpdateInquiryReqDto;
import com.example.Petbulance_BE.domain.inquiry.dto.response.*;
import com.example.Petbulance_BE.domain.inquiry.service.InquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inquiries")
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;

    @PostMapping
    public CreateInquiryResDto createInquiry(@RequestBody @Valid CreateInquiryReqDto dto) {
        return inquiryService.createInquiry(dto);
    }

    @PutMapping("/{inquiryId}")
    public UpdateInquiryResDto updateInquiry(@PathVariable("inquiryId") Long inquiryId, @RequestBody @Valid UpdateInquiryReqDto dto) {
        return inquiryService.updateInquiry(dto, inquiryId);
    }

    @DeleteMapping("/{inquiryId}")
    public DeleteInquiryResDto deleteInquiry(@PathVariable("inquiryId") Long inquiryId) {
        return inquiryService.deleteInquiry(inquiryId);
    }

    @GetMapping
    public List<InquiryListResDto> inquiryList(@RequestParam(required = false) Long lastInquiryId,
                                               @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(0, pageSize);
        return inquiryService.inquiryList(pageable, lastInquiryId);
    }

    @GetMapping("/{inquiryId}")
    public DetailInquiryResDto detailInquiry(@PathVariable("inquiryId") Long inquiryId) {
        return inquiryService.detailInquiry(inquiryId);
    }
}
