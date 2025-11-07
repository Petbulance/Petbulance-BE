package com.example.Petbulance_BE.domain.qna.controller;

import com.example.Petbulance_BE.domain.qna.dto.response.DeleteQnaResDto;
import com.example.Petbulance_BE.domain.qna.dto.request.CreateQnaReqDto;
import com.example.Petbulance_BE.domain.qna.dto.response.CreateQnaResDto;
import com.example.Petbulance_BE.domain.qna.dto.response.DetailQnaResDto;
import com.example.Petbulance_BE.domain.qna.dto.response.PagingQnaListResDto;
import com.example.Petbulance_BE.domain.qna.service.QnaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/qna")
public class QnaController {
    private final QnaService qnaService;

    @PostMapping
    public CreateQnaResDto createQna(@RequestBody @Valid CreateQnaReqDto dto) {
        return qnaService.createQna(dto);
    }

    @GetMapping
    public PagingQnaListResDto qnaList(@RequestParam(required = false) Long lastQnaId,
                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(0, pageSize);
        return qnaService.qnaList(lastQnaId, pageable);
    }

    @GetMapping("/{qnaId}")
    public DetailQnaResDto detailQna(@PathVariable("qnaId") Long qnaId) {
        return qnaService.detailQna(qnaId);
    }

    @DeleteMapping("/{qnaId}")
    public DeleteQnaResDto deleteQna(@PathVariable("qnaId") Long qnaId) {
        return qnaService.deleteQna(qnaId);
    }
}
