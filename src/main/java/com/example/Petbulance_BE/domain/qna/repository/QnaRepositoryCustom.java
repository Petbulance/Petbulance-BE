package com.example.Petbulance_BE.domain.qna.repository;

import com.example.Petbulance_BE.domain.qna.dto.response.PagingAdminQnaListResDto;
import com.example.Petbulance_BE.domain.qna.dto.response.PagingQnaListResDto;
import com.example.Petbulance_BE.domain.user.entity.Users;
import org.springframework.data.domain.Pageable;

public interface QnaRepositoryCustom {
    PagingQnaListResDto findQnaList(Users currentUser, Long lastQnaId, Pageable pageable);
    PagingAdminQnaListResDto adminQnaList(int page, int size);
}
