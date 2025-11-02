package com.example.Petbulance_BE.domain.qna.service;

import com.example.Petbulance_BE.domain.qna.dto.request.CreateQnaReqDto;
import com.example.Petbulance_BE.domain.qna.dto.response.CreateQnaResDto;
import com.example.Petbulance_BE.domain.qna.dto.response.PagingQnaListResDto;
import com.example.Petbulance_BE.domain.qna.entity.Qna;
import com.example.Petbulance_BE.domain.qna.repository.QnaRepository;
import com.example.Petbulance_BE.domain.qna.type.QnaStatus;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class QnaService {
    private final QnaRepository qnaRepository;
    public CreateQnaResDto createQna(CreateQnaReqDto dto) {
        if (dto.getContent() == null || dto.getContent().isBlank() || dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new CustomException(ErrorCode.EMPTY_QNA_CONTENT);
        }

        Users currentUser = UserUtil.getCurrentUser();
        Qna qna = Qna.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .password(dto.getPassword()) // 암호화
                .status(QnaStatus.ANSWER_WAITING)
                .createdAt(LocalDateTime.now())
                .user(currentUser)
                .build();

        qnaRepository.save(qna);
        return CreateQnaResDto.from(qna);
    }

    public PagingQnaListResDto qnaList(Long lastQnaId, Pageable pageable) {
        Users currentUser = UserUtil.getCurrentUser();
        return qnaRepository.findQnaList(currentUser, lastQnaId, pageable);
    }
}
