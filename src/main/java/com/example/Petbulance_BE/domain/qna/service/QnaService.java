package com.example.Petbulance_BE.domain.qna.service;

import com.example.Petbulance_BE.domain.qna.dto.request.CreateQnaReqDto;
import com.example.Petbulance_BE.domain.qna.dto.request.UpdateQnaReqDto;
import com.example.Petbulance_BE.domain.qna.dto.response.*;
import com.example.Petbulance_BE.domain.qna.entity.Qna;
import com.example.Petbulance_BE.domain.qna.repository.QnaRepository;
import com.example.Petbulance_BE.domain.qna.type.QnaStatus;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.util.UserUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class QnaService {
    private final QnaRepository qnaRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @CacheEvict(value = {"qnaList", "qnaDetail"}, allEntries = true)
    public CreateQnaResDto createQna(CreateQnaReqDto dto) {

        Users currentUser = UserUtil.getCurrentUser();
        Qna qna = Qna.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .status(QnaStatus.ANSWER_WAITING)
                .createdAt(LocalDateTime.now())
                .user(currentUser)
                .build();

        qnaRepository.save(qna);
        return CreateQnaResDto.from(qna);
    }

    @Transactional
    @CacheEvict(value = {"qnaList", "qnaDetail"}, allEntries = true)
    public UpdateQnaResDto updateQna(@Valid UpdateQnaReqDto dto, Long qnaId) {
        Qna qna = getQna(qnaId);
        verifyQnaUer(qna, UserUtil.getCurrentUser());
        return UpdateQnaResDto.from(qna);
    }

    @Transactional
    @CacheEvict(value = {"qnaList", "qnaDetail"}, allEntries = true)
    public DeleteQnaResDto deleteQna(Long qnaId) {
        Qna qna = getQna(qnaId);
        verifyQnaUer(qna, UserUtil.getCurrentUser());
        qnaRepository.delete(qna);
        return new DeleteQnaResDto(qnaId, "Q&A 삭제가 완료되었습니다.");
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "qnaList", key = "#currentUser.id + '-' + #lastQnaId + '-' + #pageable.pageNumber")
    public PagingQnaListResDto qnaList(Long lastQnaId, Pageable pageable) {
        Users currentUser = UserUtil.getCurrentUser();
        return qnaRepository.findQnaList(currentUser, lastQnaId, pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "qnaDetail", key = "#qnaId")
    public DetailQnaResDto detailQna(Long qnaId) {
        Qna qna = getQna(qnaId);
        verifyQnaUer(qna, UserUtil.getCurrentUser());

        return DetailQnaResDto.from(qna);
    }

    private Qna getQna(Long qnaId) {
        return qnaRepository.findById(qnaId).orElseThrow(() ->
                new CustomException(ErrorCode.QNA_NOT_FOUND));
    }

    private void verifyQnaUer(Qna qna, Users currentUser) {
        if(!qna.getUser().equals(currentUser)) {
            throw new CustomException(ErrorCode.FORBIDDEN_QNA_ACCESS);
        }
    }
}
