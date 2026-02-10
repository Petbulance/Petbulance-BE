package com.example.Petbulance_BE.domain.board.controller;

import com.example.Petbulance_BE.domain.board.dto.response.BoardListResDto;
import com.example.Petbulance_BE.domain.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {
    private final BoardService boardService;

    @GetMapping
    public List<BoardListResDto> boardList() {
        return boardService.boardList();
    }
}
