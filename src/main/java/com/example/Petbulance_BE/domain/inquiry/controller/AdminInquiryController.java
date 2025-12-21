package com.example.Petbulance_BE.domain.inquiry.controller;

import com.example.Petbulance_BE.domain.inquiry.dto.request.AnswerInquiryReqDto;
import com.example.Petbulance_BE.domain.inquiry.dto.request.CreateInquiryReqDto;
import com.example.Petbulance_BE.domain.inquiry.dto.request.UpdateInquiryReqDto;
import com.example.Petbulance_BE.domain.inquiry.dto.response.*;
import com.example.Petbulance_BE.domain.inquiry.service.InquiryService;
import com.example.Petbulance_BE.domain.qna.dto.request.AnswerQnaReqDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/inquiries")
@RequiredArgsConstructor
public class AdminInquiryController {
    private final InquiryService inquiryService;


    @GetMapping
    public PagingAdminInquiryListResDto adminInquiryList(@RequestParam(required = false) Long lastInquiryId,
                                               @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(0, pageSize);
        return inquiryService.adminInquiryList(pageable, lastInquiryId);
    }

    @GetMapping("/{inquiryId}")
    public DetailInquiryResDto detailInquiry(@PathVariable("inquiryId") Long inquiryId) {
        return inquiryService.detailInquiry(inquiryId);
    }

    @PatchMapping("/{inquiryId}")
    public AnswerInquiryResDto answerInquiry(@PathVariable("inquiryId") Long inquiryId, @RequestBody @Valid AnswerInquiryReqDto reqDto) {
        return inquiryService.answerInquiry(inquiryId, reqDto);
    }
}
