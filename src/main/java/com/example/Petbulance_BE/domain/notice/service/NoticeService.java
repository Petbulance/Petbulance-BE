package com.example.Petbulance_BE.domain.notice.service;

import com.example.Petbulance_BE.domain.adminlog.entity.AdminActionLog;
import com.example.Petbulance_BE.domain.adminlog.repository.AdminActionLogRepository;
import com.example.Petbulance_BE.domain.adminlog.type.*;
import com.example.Petbulance_BE.domain.banner.entity.Banner;
import com.example.Petbulance_BE.domain.notice.dto.request.*;
import com.example.Petbulance_BE.domain.notice.dto.response.*;
import com.example.Petbulance_BE.domain.notice.entity.Notice;
import com.example.Petbulance_BE.domain.notice.entity.NoticeFile;
import com.example.Petbulance_BE.domain.notice.repository.NoticeFileRepository;
import com.example.Petbulance_BE.domain.notice.repository.NoticeRepository;
import com.example.Petbulance_BE.domain.notice.type.PostStatus;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.common.s3.S3Service;
import com.example.Petbulance_BE.global.util.UserUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
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
    private final AdminActionLogRepository adminActionLogRepository;

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
        Users currentUser = UserUtil.getCurrentUser();
        adminActionLogRepository.save(AdminActionLog.builder()
                .actorType(AdminActorType.ADMIN)
                .admin(currentUser)
                .pageType(AdminPageType.CONTENT_MANAGEMENT)
                .actionType(AdminActionType.READ)
                .targetType(AdminTargetType.CONTENT_LIST)
                .resultType(AdminActionResult.SUCCESS)
                .description("[조회] 콘텐츠 관리 리스트 진입")
                .build()
        );

        return noticeRepository.adminNoticeList(page, size);
    }

    @CacheEvict(value = "adminNotice", allEntries = true)
    @Transactional
    public NoticeResDto createNotice(@Valid CreateNoticeReqDto reqDto) {
        Users currentUser = UserUtil.getCurrentUser();

        Banner banner = null;
        if (reqDto.isBannerRegistered()) {
            // DTO에서 넘어온 이미지가 Full URL일 경우를 대비해 Key만 추출
            String bannerKey = extractKeyFromUrl(reqDto.getBannerInfo().getImageUrl());

            if (!s3Service.doesObjectExist(bannerKey)) {
                throw new CustomException(ErrorCode.FAIL_IMAGE_UPLOAD);
            }

            banner = Banner.builder()
                    .startDate(reqDto.getBannerInfo().getStartDate())
                    .endDate(reqDto.getBannerInfo().getEndDate())
                    .fileUrl(reqDto.getBannerInfo().getImageUrl()) // 또는 s3Service.getResizedObjectUrl(bannerKey)
                    .build();
        }

        // 공지사항 파일 존재 여부 확인
        for (String fileUrl : reqDto.getFileUrls()) {
            if (!s3Service.doesObjectExist(extractKeyFromUrl(fileUrl))) {
                throw new CustomException(ErrorCode.FAIL_FILE_UPLOAD);
            }
        }

        Notice notice = noticeRepository.save(
                Notice.builder()
                        .user(currentUser)
                        .noticeStatus(reqDto.getNoticeStatus())
                        .postStatus(reqDto.getPostStatus())
                        .title(reqDto.getTitle())
                        .content(reqDto.getContent())
                        .bannerRegistered(reqDto.isBannerRegistered())
                        .banner(banner)
                        .build()
        );

        if(!reqDto.getFileUrls().isEmpty()) {
            adminActionLogRepository.save(AdminActionLog.builder()
                    .actorType(AdminActorType.ADMIN)
                    .admin(currentUser)
                    .pageType(AdminPageType.CONTENT_MANAGEMENT)
                    .actionType(AdminActionType.UPLOAD)
                    .targetType(AdminTargetType.FILE)
                    .resultType(AdminActionResult.SUCCESS)
                    .description(String.format("[업로드] %d번 공지 첨부파일 업로드", notice.getId()))
                    .build()
            );
        }

        // NoticeFile 저장 로직 (필요 시 호출)
        saveNoticeFilesOrThrow(notice, reqDto.getFileUrls());

        adminActionLogRepository.save(AdminActionLog.builder()
                .actorType(AdminActorType.ADMIN)
                .admin(currentUser)
                .pageType(AdminPageType.CONTENT_MANAGEMENT)
                .actionType(AdminActionType.CREATE)
                .targetType(AdminTargetType.NOTICE)
                .resultType(AdminActionResult.SUCCESS)
                .description(String.format("[생성] 신규 공지 %s 등록", reqDto.getTitle()))
                .build()
        );

        return new NoticeResDto(notice.getId(), "공지사항이 정상적으로 작성되었습니다.");
    }

    @CacheEvict(value = "adminNotice", allEntries = true)
    @Transactional
    public UpdateNoticeResDto updateNotice(Long noticeId, UpdateNoticeReqDto reqDto) {
        Notice notice = getNotice(noticeId); // 수정하고자하는 공지사항
        Users currentUser = UserUtil.getCurrentUser();

        // 추가하고자하는 파일이 있다면 최대 첨부 파일 갯수를 넘는지 확인
        int adding = reqDto.getAddFiles() == null ? 0 : reqDto.getAddFiles().size();
        validateFileCount(notice.getFiles().size(), adding);

        // 파일 삭제
        if (reqDto.getDeleteFileIds() != null && !reqDto.getDeleteFileIds().isEmpty()) {
            noticeFileRepository.findAllById(reqDto.getDeleteFileIds()) // db 상에서 삭제
                    .forEach(file -> {
                        s3Service.deleteObject(extractKeyFromUrl(file.getFileUrl())); // s3 상에서 삭제
                        notice.removeFile(file); // orphanRemoval
                    });
        }

        // 파일 추가
        if (adding > 0) {
            saveNoticeFilesOrThrow(notice, reqDto.getAddFiles());
        }

        String changeImageUrl = reqDto.getBannerInfo().getImageUrl();
        if(changeImageUrl != null) {{
            if(!changeImageUrl.equals(notice.getBanner().getFileUrl())) {
                // 배너 이미지를 변경하고자 함
                String key = extractKeyFromUrl(changeImageUrl);
                if (!s3Service.doesObjectExist(key)) {
                    throw new CustomException(ErrorCode.FAIL_IMAGE_UPLOAD);
                }
            }
        }}

        if(!reqDto.getPostStatus().equals(notice.getPostStatus())) {
            adminActionLogRepository.save(AdminActionLog.builder()
                    .actorType(AdminActorType.ADMIN)
                    .admin(currentUser)
                    .pageType(AdminPageType.CONTENT_MANAGEMENT)
                    .actionType(AdminActionType.UPDATE)
                    .targetType(AdminTargetType.NOTICE)
                    .resultType(AdminActionResult.SUCCESS)
                    .description(String.format("[상태 변경] %d번 공지 %S", noticeId, reqDto.getPostStatus().equals(PostStatus.ACTIVE) ? "중단->게시" : "게시->중단"))
                    .build()
            );
        }

        notice.update(reqDto);

        adminActionLogRepository.save(AdminActionLog.builder()
                .actorType(AdminActorType.ADMIN)
                .admin(currentUser)
                .pageType(AdminPageType.CONTENT_MANAGEMENT)
                .actionType(AdminActionType.UPDATE)
                .targetType(AdminTargetType.NOTICE)
                .resultType(AdminActionResult.SUCCESS)
                .description(String.format("[수정] %d번 공지 본문 내용 수정", noticeId))
                .build()
        );

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
        if (fileUrl == null || !fileUrl.contains(".com/")) {
            return fileUrl; // 이미 Key 형태인 경우 그대로 반환
        }
        // ".com/" 이후의 모든 문자열을 Key로 간주
        return fileUrl.substring(fileUrl.indexOf(".com/") + 5);
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

    private void saveNoticeFilesOrThrow(Notice notice, List<String> urls) {
        if (urls == null) return;

        for (String url : urls) {
            String key = extractKeyFromUrl(url);
            if (!s3Service.doesObjectExist(key)) {
                throw new CustomException(ErrorCode.FAIL_FILE_UPLOAD);
            }

            noticeFileRepository.save(
                    NoticeFile.builder()
                            .notice(notice)
                            .fileUrl(url)
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

    @Transactional(readOnly = true)
    public AdminDetailNoticeResDto adminDetailNotice(Long noticeId) {
        // 1. 공지사항 조회 (Fetch Join을 사용하지 않는다면 Banner와 Files는 지연 로딩됨)
        Notice notice = getNotice(noticeId);

        // 2. 파일 리스트 변환
        List<AdminDetailNoticeResDto.FileResDto> fileList = notice.getFiles().stream()
                .map(file -> new AdminDetailNoticeResDto.FileResDto(
                        file.getId(),
                        file.getFileUrl(),
                        file.getFileName()
                ))
                .toList();

        // 3. 배너 정보 변환 (배너 등록 여부 확인)
        AdminDetailNoticeResDto.BannerInfoResDto bannerDto = null;
        if (notice.isBannerRegistered() && notice.getBanner() != null) {
            Banner banner = notice.getBanner();
            bannerDto = new AdminDetailNoticeResDto.BannerInfoResDto(
                    banner.getStartDate(),
                    banner.getEndDate(),
                    banner.getFileUrl()
            );
        }

        // 4. 최종 DTO 조립 및 반환
        return new AdminDetailNoticeResDto(
                notice.getPostStatus(),
                notice.getNoticeStatus(),
                notice.getTitle(),
                notice.getContent(),
                fileList,
                bannerDto
        );
    }
}
