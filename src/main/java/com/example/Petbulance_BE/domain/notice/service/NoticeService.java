package com.example.Petbulance_BE.domain.notice.service;

import com.example.Petbulance_BE.domain.notice.dto.response.DetailNoticeResDto;
import com.example.Petbulance_BE.domain.notice.dto.response.PagingNoticeListResDto;
import com.example.Petbulance_BE.domain.notice.entity.Notice;
import com.example.Petbulance_BE.domain.notice.entity.NoticeFile;
import com.example.Petbulance_BE.domain.notice.repository.NoticeFileRepository;
import com.example.Petbulance_BE.domain.notice.repository.NoticeRepository;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "noticeList", key = "#lastNoticeId != null ? #lastNoticeId : 'first'")
    public PagingNoticeListResDto noticeList(Long lastNoticeId, Pageable pageable) {
        Notice notice = null;
        LocalDateTime lastCreatedAt = null;
        Boolean lastIsImportant = null;

        if (lastNoticeId != null) {
            notice = noticeRepository.findById(lastNoticeId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));
            lastCreatedAt = notice.getCreatedAt();
            lastIsImportant = notice.isImportant();
        }

        return noticeRepository.findNoticeList(lastNoticeId, lastCreatedAt, lastIsImportant, pageable);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "noticeDetail", key = "#noticeId")
    public DetailNoticeResDto detailNotice(Long noticeId) {
        Notice n = noticeRepository.findById(noticeId).orElseThrow(
                () -> new CustomException(ErrorCode.NOTICE_NOT_FOUND)
        );

        List<NoticeFile> files = noticeFileRepository.findAllByNoticeId(noticeId);

        Notice prev = noticeRepository.findPreviousNotice(noticeId);
        Notice next = noticeRepository.findNextNotice(noticeId);

        return DetailNoticeResDto.from(n, files, prev, next);
    }
}
