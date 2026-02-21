package com.example.Petbulance_BE.domain.notice.controller;

import com.example.Petbulance_BE.domain.notice.dto.request.CreateNoticeReqDto;
import com.example.Petbulance_BE.domain.notice.dto.request.UpdateNoticeReqDto;
import com.example.Petbulance_BE.domain.notice.dto.response.*;
import com.example.Petbulance_BE.domain.notice.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/notices")
@RequiredArgsConstructor
public class AdminNoticeController {
    private final NoticeService noticeService;

    @GetMapping
    public PagingAdminNoticeListResDto adminNoticeList(@RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "20") int size) {
        return noticeService.adminNoticeList(page, size);
    }

    @PostMapping
    public NoticeResDto createNotice(@RequestBody @Valid CreateNoticeReqDto reqDto) {
        return noticeService.createNotice(reqDto);
    }

    @PutMapping("/{noticeId}")
    public UpdateNoticeResDto updateNotice(@PathVariable("noticeId") Long noticeId, @RequestBody @Valid UpdateNoticeReqDto reqDto) {
        return noticeService.updateNotice(noticeId, reqDto);
    }

    @GetMapping("/{noticeId}")
    public AdminDetailNoticeResDto adminDetailNotice(@PathVariable("noticeId") Long noticeId) {
        return noticeService.adminDetailNotice(noticeId);
    }

    @DeleteMapping("/{noticeId}")
    public AdminDeleteNotice adminDeleteNotice(@PathVariable("noticeId") Long noticeId) {
        return noticeService.adminDeleteNotice(noticeId);
    }

}
