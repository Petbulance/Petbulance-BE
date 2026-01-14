package com.example.Petbulance_BE.domain.notice.controller;

import com.example.Petbulance_BE.domain.notice.dto.request.AddFileReqDto;
import com.example.Petbulance_BE.domain.notice.dto.request.CreateNoticeReqDto;
import com.example.Petbulance_BE.domain.notice.dto.request.NoticeImageCheckReqDto;
import com.example.Petbulance_BE.domain.notice.dto.request.UpdateNoticeReqDto;
import com.example.Petbulance_BE.domain.notice.dto.response.*;
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
    public PagingAdminNoticeListResDto adminNoticeList(@RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "20") int size) {
        return noticeService.adminNoticeList(page, size);
    }

    @PostMapping
    public NoticeResDto createNotice(@RequestBody @Valid CreateNoticeReqDto reqDto) {
        return noticeService.createNotice(reqDto);
    }

    @GetMapping("/save/success")
    public NoticeImageCheckResDto noticeFileSaveCheckProcess(@RequestBody NoticeImageCheckReqDto reqDto) {
        noticeService.noticeFileSaveCheckProcess(reqDto);
        return new NoticeImageCheckResDto("성공적으로 이미지가 등록되었습니다.");

    }

    @PutMapping("{noticeId}")
    public UpdateNoticeResDto updateNotice(@PathVariable("noticeId") Long noticeId, @RequestBody @Valid UpdateNoticeReqDto reqDto) {
        return noticeService.updateNotice(noticeId, reqDto);
    }

    @GetMapping("/add/files")
    public AddFileResDto addFiles(@RequestBody @Valid AddFileReqDto reqDto) {
        return noticeService.addFiles(reqDto);
    }


}
