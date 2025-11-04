package com.example.Petbulance_BE.domain.qna.service;

import com.example.Petbulance_BE.domain.qna.dto.request.CreateQnaReqDto;
import com.example.Petbulance_BE.domain.qna.dto.response.CreateQnaResDto;
import com.example.Petbulance_BE.domain.qna.dto.response.DeleteQnaResDto;
import com.example.Petbulance_BE.domain.qna.dto.response.DetailQnaResDto;
import com.example.Petbulance_BE.domain.qna.dto.response.PagingQnaListResDto;
import com.example.Petbulance_BE.domain.qna.entity.Qna;
import com.example.Petbulance_BE.domain.qna.repository.QnaRepository;
import com.example.Petbulance_BE.domain.qna.type.QnaStatus;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class QnaService {
    private final QnaRepository qnaRepository;
    private final PasswordEncoder passwordEncoder;

    @CacheEvict(value = {"qnaList", "qnaDetail"}, allEntries = true)
    public CreateQnaResDto createQna(CreateQnaReqDto dto) {
        String encodedPassword = dto.getPassword() != null
                ? passwordEncoder.encode(dto.getPassword())
                : null;

        Users currentUser = UserUtil.getCurrentUser();
        Qna qna = Qna.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .password(encodedPassword) // 암호화
                .status(QnaStatus.ANSWER_WAITING)
                .createdAt(LocalDateTime.now())
                .user(currentUser)
                .build();

        qnaRepository.save(qna);
        return CreateQnaResDto.from(qna);
    }

    @Cacheable(value = "qnaList", key = "#currentUser.id + '-' + #lastQnaId + '-' + #pageable.pageNumber")
    public PagingQnaListResDto qnaList(Long lastQnaId, Pageable pageable) {
        Users currentUser = UserUtil.getCurrentUser();
        return qnaRepository.findQnaList(currentUser, lastQnaId, pageable);
    }

    @Cacheable(value = "qnaDetail", key = "#qnaId")
    public DetailQnaResDto detailQna(Long qnaId, String password) {
        Qna qna = getQna(qnaId);
        verifyQnaUer(qna, UserUtil.getCurrentUser(), password); // 비밀번호 암호화 필요

        return DetailQnaResDto.from(qna);
    }

    private Qna getQna(Long qnaId) {
        return qnaRepository.findById(qnaId).orElseThrow(() ->
                new CustomException(ErrorCode.QNA_NOT_FOUND));
    }

    @CacheEvict(value = {"qnaList", "qnaDetail"}, allEntries = true)
    public DeleteQnaResDto deleteQna(Long qnaId) {
        Qna qna = getQna(qnaId);
        verifyQnaUer(qna, UserUtil.getCurrentUser());
        qnaRepository.delete(qna);
        return new DeleteQnaResDto(qnaId, "Q&A 삭제가 완료되었습니다.");
    }

    private void verifyQnaUer(Qna qna, Users currentUser, String password) {
        boolean isOwner = qna.getUser().equals(currentUser);
        boolean passwordMatch = (password == null) || passwordEncoder.matches(password, qna.getPassword());

        if (!isOwner || !passwordMatch) {
            throw new CustomException(ErrorCode.FORBIDDEN_QNA_ACCESS);
        }
    }

    private void verifyQnaUer(Qna qna, Users currentUser) {
        if(!qna.getUser().equals(currentUser)) {
            throw new CustomException(ErrorCode.FORBIDDEN_QNA_ACCESS);
        }
    }
}
