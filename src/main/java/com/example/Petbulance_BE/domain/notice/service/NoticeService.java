package com.example.Petbulance_BE.domain.notice.service;

import com.example.Petbulance_BE.domain.notice.dto.request.CreateNoticeReqDto;
import com.example.Petbulance_BE.domain.notice.dto.response.CreateNoticeResDto;
import com.example.Petbulance_BE.domain.notice.dto.response.DetailNoticeResDto;
import com.example.Petbulance_BE.domain.notice.dto.response.PagingAdminNoticeListResDto;
import com.example.Petbulance_BE.domain.notice.dto.response.PagingNoticeListResDto;
import com.example.Petbulance_BE.domain.notice.entity.Notice;
import com.example.Petbulance_BE.domain.notice.entity.NoticeFile;
import com.example.Petbulance_BE.domain.notice.repository.NoticeFileRepository;
import com.example.Petbulance_BE.domain.notice.repository.NoticeRepository;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import jakarta.validation.Valid;
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
    public PagingNoticeListResDto noticeList(Long lastNoticeId, Pageable pageable) {

        LocalDateTime lastCreatedAt = null;

        if (lastNoticeId != null) {
            Notice notice = noticeRepository.findById(lastNoticeId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));
            lastCreatedAt = notice.getCreatedAt();
        }

        return noticeRepository.findNoticeList(lastNoticeId, lastCreatedAt, pageable);
    }


    @Transactional(readOnly = true)
    public DetailNoticeResDto detailNotice(Long noticeId) {
        Notice n = noticeRepository.findById(noticeId).orElseThrow(
                () -> new CustomException(ErrorCode.NOTICE_NOT_FOUND)
        );

        List<NoticeFile> files = noticeFileRepository.findAllByNoticeId(noticeId);

        Notice prev = noticeRepository.findPreviousNotice(noticeId);
        Notice next = noticeRepository.findNextNotice(noticeId);

        return DetailNoticeResDto.from(n, files, prev, next);
    }

    @Transactional
    public CreateNoticeResDto createNotice(@Valid CreateNoticeReqDto reqDto) {

        Notice notice = Notice.builder()
                .noticeStatus(reqDto.getNoticeStatus())
                .title(reqDto.getTitle())
                .content(reqDto.getContent())
                .build();

        if (reqDto.getFileUrl() != null) {
            NoticeFile file = NoticeFile.builder()
                    .fileUrl(reqDto.getFileUrl())
                    .fileName(reqDto.getFileName())
                    .fileType(reqDto.getFileType())
                    .build();

            notice.addFile(file);
        }

        noticeRepository.save(notice);

        return new CreateNoticeResDto("공지사항이 정상적으로 작성되었습니다.");
    }

}
