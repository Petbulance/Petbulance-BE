package com.example.Petbulance_BE.domain.qna.service;

import com.example.Petbulance_BE.domain.adminlog.entity.AdminActionLog;
import com.example.Petbulance_BE.domain.adminlog.repository.AdminActionLogRepository;
import com.example.Petbulance_BE.domain.adminlog.type.*;
import com.example.Petbulance_BE.domain.qna.dto.request.AnswerQnaReqDto;
import com.example.Petbulance_BE.domain.qna.dto.request.CreateQnaReqDto;
import com.example.Petbulance_BE.domain.qna.dto.request.UpdateQnaReqDto;
import com.example.Petbulance_BE.domain.qna.dto.response.*;
import com.example.Petbulance_BE.domain.qna.entity.Qna;
import com.example.Petbulance_BE.domain.qna.repository.QnaRepository;
import com.example.Petbulance_BE.domain.qna.type.QnaStatus;
import com.example.Petbulance_BE.domain.report.type.ReportType;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.common.type.Role;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class QnaService {
    private final QnaRepository qnaRepository;
    private final AdminActionLogRepository adminActionLogRepository;


    @Transactional
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
    public UpdateQnaResDto updateQna(@Valid UpdateQnaReqDto dto, Long qnaId) {
        Qna qna = getQna(qnaId);
        verifyQnaUer(qna, UserUtil.getCurrentUser());
        return UpdateQnaResDto.from(qna);
    }

    @Transactional
    public DeleteQnaResDto deleteQna(Long qnaId) {
        Qna qna = getQna(qnaId);
        verifyQnaUer(qna, UserUtil.getCurrentUser());
        qnaRepository.delete(qna);
        return new DeleteQnaResDto(qnaId, "Q&A 삭제가 완료되었습니다.");
    }


    @Transactional(readOnly = true)
    public PagingQnaListResDto qnaList(Long lastQnaId, Pageable pageable) {
        Users currentUser = UserUtil.getCurrentUser();
        return qnaRepository.findQnaList(currentUser, lastQnaId, pageable);
    }

    @Transactional(readOnly = true)
    public DetailQnaResDto detailQna(Long qnaId) {
        Qna qna = getQna(qnaId);
        Users currentUser = UserUtil.getCurrentUser();

        assert currentUser != null;
        boolean isAdmin = currentUser.getRole().equals(Role.ROLE_ADMIN);

        // 작성자가 아니고 관리자가 아니면 조회 불가
        if (!qna.getUser().getId().equals(currentUser.getId()) && !isAdmin) {
            throw new CustomException(ErrorCode.FORBIDDEN_QNA_ACCESS);
        }

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


    @Transactional
    public AnswerQnaResDto answerQna(Long qnaId, @Valid AnswerQnaReqDto reqDto) {
        Qna qna = getQna(qnaId);

        if (qna.getStatus() == QnaStatus.ANSWER_WAITING) {
            qna.answer(reqDto.getContent());
        } else {
            throw new CustomException(ErrorCode.ALREADY_WRITTEN_ANSWER);
        }

        Users currentUser = UserUtil.getCurrentUser();
        adminActionLogRepository.save(AdminActionLog.builder()
                .actorType(AdminActorType.ADMIN)
                .admin(currentUser)
                .pageType(AdminPageType.CUSTOMER_CENTER)
                .actionType(AdminActionType.UPDATE)
                .targetType(AdminTargetType.CS_ANSWER)
                .resultType(AdminActionResult.SUCCESS)
                .description(String.format("[작성] %d번 1:1 문의 답변 발송 및 상태 변경 (대기 -> 처리)", qnaId))
                .build()
        );

        return new AnswerQnaResDto("답변이 정상적으로 작성되었습니다.");
    }

    @Transactional(readOnly = true)
    public PagingAdminQnaListResDto adminQnaList(int page, int size) {

        return qnaRepository.adminQnaList(page, size);
    }
}
