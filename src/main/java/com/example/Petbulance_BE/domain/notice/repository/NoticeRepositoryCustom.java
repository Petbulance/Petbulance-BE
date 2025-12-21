package com.example.Petbulance_BE.domain.notice.repository;

import com.example.Petbulance_BE.domain.notice.dto.response.PagingAdminNoticeListResDto;
import com.example.Petbulance_BE.domain.notice.dto.response.PagingNoticeListResDto;
import com.example.Petbulance_BE.domain.notice.entity.Notice;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface NoticeRepositoryCustom {
    PagingNoticeListResDto findNoticeList(Long lastNoticeId, LocalDateTime lastCreatedAt, Pageable pageable);
    Notice findPreviousNotice(Long noticeId);
    Notice findNextNotice(Long noticeId);
    PagingAdminNoticeListResDto adminNoticeList(Long lastNoticeId, Pageable pageable)

}
