package com.example.Petbulance_BE.domain.qna.controller;

import com.example.Petbulance_BE.domain.qna.dto.request.AnswerQnaReqDto;
import com.example.Petbulance_BE.domain.qna.dto.response.AnswerQnaResDto;
import com.example.Petbulance_BE.domain.qna.dto.response.PagingAdminQnaListResDto;
import com.example.Petbulance_BE.domain.qna.service.QnaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/qna")
public class AdminQnaController {
    private final QnaService qnaService;

    @GetMapping
    public PagingAdminQnaListResDto adminQnaList(@RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "20") int size) {
        return qnaService.adminQnaList(page, size);
    }

    @PatchMapping("/{qnaId}")
    public AnswerQnaResDto answerQna(@PathVariable("qnaId") Long qnaId, @RequestBody @Valid AnswerQnaReqDto reqDto) {
        return qnaService.answerQna(qnaId, reqDto);
    }
}
