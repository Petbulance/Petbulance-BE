package com.example.Petbulance_BE.domain.notice.service;

import com.example.Petbulance_BE.domain.notice.dto.request.CreateNoticeReqDto;
import com.example.Petbulance_BE.domain.notice.dto.request.UpdateNoticeReqDto;
import com.example.Petbulance_BE.domain.notice.dto.response.NoticeResDto;
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
    public NoticeResDto createNotice(@Valid CreateNoticeReqDto reqDto) {

        Notice notice = Notice.builder()
                .noticeStatus(reqDto.getNoticeStatus())
                .postStatus(reqDto.getPostStatus())
                .title(reqDto.getTitle())
                .content(reqDto.getContent())
                .postStartDate(reqDto.getStartDate())
                .postEndDate(reqDto.getEndDate())
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

        return new NoticeResDto("공지사항이 정상적으로 작성되었습니다.");
    }


    public NoticeResDto updateNotice(Long noticeId, @Valid UpdateNoticeReqDto reqDto) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));
        notice.update(reqDto);

        // 파일 수정 관련 로직 (나중에)

        return new NoticeResDto("공지사항이 정상적으로 수정되었습니다.");
    }

    @Transactional(readOnly = true)
    public PagingAdminNoticeListResDto adminNoticeList(int page, int size) {
        return noticeRepository.adminNoticeList(page, size);
    }
}
