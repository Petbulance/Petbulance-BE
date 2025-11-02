package com.example.Petbulance_BE.domain.qna.controller;

import com.example.Petbulance_BE.domain.qna.dto.request.CreateQnaReqDto;
import com.example.Petbulance_BE.domain.qna.dto.response.CreateQnaResDto;
import com.example.Petbulance_BE.domain.qna.service.QnaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/qna")
public class QnaController {
    private final QnaService qnaService;

    @PostMapping
    public CreateQnaResDto createQna(@Valid @RequestBody CreateQnaReqDto dto) {
        return qnaService.createQna(dto);
    }
}
