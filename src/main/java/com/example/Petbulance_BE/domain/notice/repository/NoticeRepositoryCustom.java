package com.example.Petbulance_BE.domain.notice.repository;

import com.example.Petbulance_BE.domain.notice.dto.response.AdminDetailNoticeResDto;
import com.example.Petbulance_BE.domain.notice.dto.response.PagingAdminNoticeListResDto;
import com.example.Petbulance_BE.domain.notice.dto.response.PagingNoticeListResDto;
import com.example.Petbulance_BE.domain.notice.entity.Notice;
import org.springframework.data.domain.Pageable;

public interface NoticeRepositoryCustom {
    PagingNoticeListResDto findNoticeList(Long lastNoticeId, int pageSize);
    Notice findPreviousNotice(Long noticeId);
    Notice findNextNotice(Long noticeId);
    PagingAdminNoticeListResDto adminNoticeList(int page, int size);
}
