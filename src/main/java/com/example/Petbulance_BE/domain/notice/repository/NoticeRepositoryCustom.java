package com.example.Petbulance_BE.domain.notice.repository;

import com.example.Petbulance_BE.domain.notice.dto.response.PagingNoticeListResDto;
import com.example.Petbulance_BE.domain.notice.entity.Notice;
import org.springframework.data.domain.Pageable;

public interface NoticeRepositoryCustom {
    PagingNoticeListResDto findNoticeList(Long lastNoticeId, Pageable pageable);
    Notice findPreviousNotice(Long noticeId);
    Notice findNextNotice(Long noticeId);
}
