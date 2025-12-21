package com.example.Petbulance_BE.domain.notice.controller;

import com.example.Petbulance_BE.domain.notice.dto.request.CreateNoticeReqDto;
import com.example.Petbulance_BE.domain.notice.dto.response.CreateNoticeResDto;
import com.example.Petbulance_BE.domain.notice.dto.response.DetailNoticeResDto;
import com.example.Petbulance_BE.domain.notice.dto.response.PagingAdminNoticeListResDto;
import com.example.Petbulance_BE.domain.notice.dto.response.PagingNoticeListResDto;
import com.example.Petbulance_BE.domain.notice.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/notices")
@RequiredArgsConstructor
public class AdminNoticeController {
    private final NoticeService noticeService;

    @GetMapping
    public PagingAdminNoticeListResDto adminNoticeList(@RequestParam(required = false) Long lastNoticeId,
                                                       @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(0, pageSize);
        return noticeService.adminNoticeList(lastNoticeId, pageable);
    }

    @PostMapping
    public CreateNoticeResDto createNotice(@RequestBody @Valid CreateNoticeReqDto reqDto) {
        return noticeService.createNotice(reqDto);
    }
}
