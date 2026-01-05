package com.example.Petbulance_BE.domain.notice.controller;

import com.example.Petbulance_BE.domain.notice.dto.response.DetailNoticeResDto;
import com.example.Petbulance_BE.domain.notice.dto.response.FileDownloadResDto;
import com.example.Petbulance_BE.domain.notice.dto.response.PagingNoticeListResDto;
import com.example.Petbulance_BE.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping
    public PagingNoticeListResDto noticeList(@RequestParam(required = false) Long lastNoticeId,
                                             @RequestParam(defaultValue = "10") int pageSize) {
        return noticeService.noticeList(lastNoticeId, pageSize);
    }

    @GetMapping("/{noticeId}")
    public DetailNoticeResDto detailNotice(@PathVariable("noticeId") Long noticeId) {
        return noticeService.detailNotice(noticeId);
    }

    @GetMapping("/{noticeId}/attachments/{fileId}/download")
    public FileDownloadResDto downloadFile(
            @PathVariable Long noticeId,
            @PathVariable Long fileId) {
        return noticeService.downloadNoticeFile(noticeId, fileId);
    }

}
