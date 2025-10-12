package com.example.Petbulance_BE.domain.board.service;

import com.example.Petbulance_BE.domain.board.dto.response.BoardListResDto;
import com.example.Petbulance_BE.domain.board.repository.BoardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;

    public List<BoardListResDto> boardList() {
        return boardRepository.findAll()
                .stream()
                .map(BoardListResDto::of)
                .toList();
    }
}
