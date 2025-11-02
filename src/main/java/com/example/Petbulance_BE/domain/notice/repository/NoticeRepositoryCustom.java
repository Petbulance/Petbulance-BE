package com.example.Petbulance_BE.domain.notice.repository;

import com.example.Petbulance_BE.domain.notice.dto.response.PagingNoticeListResDto;
import org.springframework.data.domain.Pageable;

public interface NoticeRepositoryCustom {
    PagingNoticeListResDto findNoticeList(Long lastNoticeId, Pageable pageable);
}
