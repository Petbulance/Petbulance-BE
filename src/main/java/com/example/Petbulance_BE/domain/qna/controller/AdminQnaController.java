package com.example.Petbulance_BE.domain.qna.controller;

import com.example.Petbulance_BE.domain.qna.dto.request.AnswerQnaReqDto;
import com.example.Petbulance_BE.domain.qna.dto.response.AnswerQnaResDto;
import com.example.Petbulance_BE.domain.qna.dto.response.PagingAdminQnaListResDto;
import com.example.Petbulance_BE.domain.qna.dto.response.PagingQnaListResDto;
import com.example.Petbulance_BE.domain.qna.service.QnaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/{qnaId}")
    public AnswerQnaResDto answerQna(@PathVariable("qnaId") Long qnaId, @RequestBody @Valid AnswerQnaReqDto reqDto) {
        return qnaService.answerQna(qnaId, reqDto);
    }
}
