package com.example.Petbulance_BE.domain.qna.controller;

import com.example.Petbulance_BE.domain.qna.dto.response.PagingAdminQnaListResDto;
import com.example.Petbulance_BE.domain.qna.dto.response.PagingQnaListResDto;
import com.example.Petbulance_BE.domain.qna.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/qna")
public class AdminQnaController {
    private final QnaService qnaService;

    @GetMapping
    public PagingAdminQnaListResDto adminQnaList(@RequestParam(required = false) Long lastQnaId,
                                                 @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(0, pageSize);
        return qnaService.adminQnaList(lastQnaId, pageable);
    }
}
