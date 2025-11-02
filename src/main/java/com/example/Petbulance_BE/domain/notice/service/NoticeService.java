package com.example.Petbulance_BE.domain.notice.service;

import com.example.Petbulance_BE.domain.notice.dto.response.PagingNoticeListResDto;
import com.example.Petbulance_BE.domain.notice.repository.NoticeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;

    public PagingNoticeListResDto noticeList(Long lastNoticeId, Pageable pageable) {
        return noticeRepository.findNoticeList(lastNoticeId, pageable);
    }
}
