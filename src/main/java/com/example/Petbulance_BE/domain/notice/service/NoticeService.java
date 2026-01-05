package com.example.Petbulance_BE.domain.notice.service;

import com.example.Petbulance_BE.domain.notice.dto.request.*;
import com.example.Petbulance_BE.domain.notice.dto.response.*;
import com.example.Petbulance_BE.domain.notice.entity.Notice;
import com.example.Petbulance_BE.domain.notice.entity.NoticeFile;
import com.example.Petbulance_BE.domain.notice.repository.NoticeFileRepository;
import com.example.Petbulance_BE.domain.notice.repository.NoticeRepository;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.common.s3.S3Service;
import com.example.Petbulance_BE.global.util.UserUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private static final int MAX_FILE_COUNT = 5;

    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;
    private final S3Service s3Service;

    @Transactional(readOnly = true)
    public PagingNoticeListResDto noticeList(Long lastNoticeId, int pageSize) {
        return noticeRepository.findNoticeList(lastNoticeId, pageSize);
    }

    @Transactional(readOnly = true)
    public DetailNoticeResDto detailNotice(Long noticeId) {
        Notice notice = getNotice(noticeId);

        List<NoticeFile> files = noticeFileRepository.findAllByNoticeId(noticeId);
        Notice prev = noticeRepository.findPreviousNotice(noticeId);
        Notice next = noticeRepository.findNextNotice(noticeId);

        return DetailNoticeResDto.from(notice, files, prev, next);
    }

    @Cacheable(
            value = "adminNotice",
            key = "'page:' + #page + ':size:' + #size",
            condition = "#page >= 1 && #page <= 5"
    )
    @Transactional(readOnly = true)
    public PagingAdminNoticeListResDto adminNoticeList(int page, int size) {
        return noticeRepository.adminNoticeList(page, size);
    }

    @CacheEvict(value = "adminNotice", allEntries = true)
    @Transactional
    public NoticeResDto createNotice(@Valid CreateNoticeReqDto reqDto) {
        Users user = UserUtil.getCurrentUser();

        Notice notice = noticeRepository.save(
                Notice.builder()
                        .noticeStatus(reqDto.getNoticeStatus())
                        .postStatus(reqDto.getPostStatus())
                        .title(reqDto.getTitle())
                        .content(reqDto.getContent())
                        .postStartDate(reqDto.getStartDate())
                        .postEndDate(reqDto.getEndDate())
                        .user(user)
                        .build()
        );

        List<NoticeResDto.UrlAndId> presignedUrls =
                generatePresignedUrls(reqDto.getFiles());

        return new NoticeResDto(
                notice.getId(),
                "공지사항이 정상적으로 작성되었습니다.",
                presignedUrls
        );
    }

    @CacheEvict(value = "adminNotice", allEntries = true)
    @Transactional
    public UpdateNoticeResDto updateNotice(Long noticeId, UpdateNoticeReqDto reqDto) {
        Notice notice = getNotice(noticeId);

        int adding = reqDto.getAddFiles() == null ? 0 : reqDto.getAddFiles().size();
        validateFileCount(notice.getFiles().size(), adding);

        // 파일 삭제
        if (reqDto.getDeleteFileIds() != null && !reqDto.getDeleteFileIds().isEmpty()) {
            noticeFileRepository.findAllById(reqDto.getDeleteFileIds())
                    .forEach(file -> {
                        s3Service.deleteObject(file.getFileUrl());
                        notice.removeFile(file); // orphanRemoval
                    });
        }

        // 파일 추가
        if (adding > 0) {
            saveNoticeFilesOrThrow(notice, reqDto.getAddFiles());
        }

        notice.update(reqDto);

        return new UpdateNoticeResDto(
                notice.getId(),
                "공지사항이 성공적으로 수정되었습니다."
        );
    }

    /* =======================
     * 파일 관련 처리
     * ======================= */

    @Transactional
    public void noticeFileSaveCheckProcess(NoticeImageCheckReqDto reqDto) {
        Notice notice = getNotice(reqDto.getNoticeId());

        try {
            saveNoticeFilesOrThrow(notice, reqDto.getKeys());
        } catch (CustomException e) {
            cleanupUploadedFiles(reqDto.getKeys());
            noticeRepository.delete(notice);
            throw e;
        }
    }

    @Transactional
    public AddFileResDto addFiles(@Valid AddFileReqDto reqDto) {
        List<AddFileResDto.UrlAndId> urls =
                generateAddFilePresignedUrls(reqDto.getAddFiles());
        return new AddFileResDto(urls);
    }

    @Transactional(readOnly = true)
    public FileDownloadResDto downloadNoticeFile(Long noticeId, Long fileId) {

        NoticeFile file = noticeFileRepository.findById(fileId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTICE_FILE_NOT_FOUND));

        if (!file.getNotice().getId().equals(noticeId)) {
            throw new CustomException(ErrorCode.INVALID_NOTICE_FILE_ACCESS);
        }

        // S3 key는 URL에서 추출
        String key = extractKeyFromUrl(file.getFileUrl());

        // presigned GET URL (60초)
        URL downloadUrl = s3Service.createPresignedGetUrl(key, 60);

        return new FileDownloadResDto(downloadUrl, file.getFileName());
    }

    // 유틸 메서드


    private String extractKeyFromUrl(String fileUrl) {
        // https://bucket-resized.s3.region.amazonaws.com/noticeImage/uuid_filename
        return fileUrl.substring(fileUrl.indexOf("noticeImage/"));
    }

    private Notice getNotice(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));
    }

    private void validateFileCount(int current, int adding) {
        if (current + adding > MAX_FILE_COUNT) {
            throw new CustomException(ErrorCode.MAX_FILE_COUNT_EXCEEDED);
        }
    }

    private List<NoticeResDto.UrlAndId> generatePresignedUrls(
            List<CreateNoticeReqDto.NoticeFileReqDto> files
    ) {
        List<NoticeResDto.UrlAndId> result = new LinkedList<>();

        for (CreateNoticeReqDto.NoticeFileReqDto file : files) {
            String key = createFileKey(file.getFilename());
            URL url = s3Service.createPresignedPutUrl(key, file.getContentType(), 300);
            result.add(new NoticeResDto.UrlAndId(url, key));
        }
        return result;
    }

    private List<AddFileResDto.UrlAndId> generateAddFilePresignedUrls(
            List<AddFileReqDto.NoticeFileReqDto> files
    ) {
        List<AddFileResDto.UrlAndId> result = new LinkedList<>();

        for (AddFileReqDto.NoticeFileReqDto file : files) {
            String key = createFileKey(file.getFilename());
            URL url = s3Service.createPresignedPutUrl(key, file.getContentType(), 300);
            result.add(new AddFileResDto.UrlAndId(url, key));
        }
        return result;
    }

    private void saveNoticeFilesOrThrow(Notice notice, List<String> keys) {
        for (String key : keys) {
            if (!s3Service.doesObjectExist(key)) {
                throw new CustomException(ErrorCode.FAIL_FILE_UPLOAD);
            }
        }

        for (String key : keys) {
            noticeFileRepository.save(
                    NoticeFile.builder()
                            .notice(notice)
                            .fileUrl(s3Service.getObject(key))
                            .fileName(extractFileName(key))
                            .build()
            );
        }
    }

    private void cleanupUploadedFiles(List<String> keys) {
        for (String key : keys) {
            try {
                s3Service.deleteObject(key);
            } catch (Exception e) {
                log.warn("S3 파일 삭제 실패: {}", key, e);
            }
        }
    }

    private String createFileKey(String filename) {
        return "noticeImage/" + UUID.randomUUID() + "_" + filename;
    }

    private String extractFileName(String key) {
        return key.split("_", 2)[1];
    }
}
