package com.example.Petbulance_BE.domain.notice.controller;

import com.example.Petbulance_BE.domain.notice.dto.response.PagingNoticeListResDto;
import com.example.Petbulance_BE.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping
    public PagingNoticeListResDto noticeList(@RequestParam(required = false) Long lastNoticeId, @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(0, pageSize);
        return noticeService.noticeList(lastNoticeId, pageable);
    }
}
