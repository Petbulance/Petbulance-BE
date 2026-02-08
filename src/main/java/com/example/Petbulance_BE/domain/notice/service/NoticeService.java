package com.example.Petbulance_BE.domain.notice.service;

import com.example.Petbulance_BE.domain.adminlog.entity.AdminActionLog;
import com.example.Petbulance_BE.domain.adminlog.repository.AdminActionLogRepository;
import com.example.Petbulance_BE.domain.adminlog.type.*;
import com.example.Petbulance_BE.domain.banner.entity.Banner;
import com.example.Petbulance_BE.domain.notice.dto.request.*;
import com.example.Petbulance_BE.domain.notice.dto.response.*;
import com.example.Petbulance_BE.domain.notice.entity.Button;
import com.example.Petbulance_BE.domain.notice.entity.Notice;
import com.example.Petbulance_BE.domain.notice.entity.NoticeFile;
import com.example.Petbulance_BE.domain.notice.repository.ButtonRepository;
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
    private final ButtonRepository buttonRepository;

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

    @Transactional(readOnly = true)
    public PagingAdminNoticeListResDto adminNoticeList(int page, int size) {

        return noticeRepository.adminNoticeList(page, size);
    }

    @Transactional
    public NoticeResDto createNotice(@Valid CreateNoticeReqDto reqDto) {
        Users currentUser = UserUtil.getCurrentUser();

        // 1. Banner 생성
        Banner banner = null;
        if (reqDto.isBannerRegistered() && reqDto.getBannerInfo() != null) {
            String bannerKey = extractKeyFromUrl(reqDto.getBannerInfo().getImageUrl());
            if (!s3Service.doesObjectExist(bannerKey)) {
                throw new CustomException(ErrorCode.FAIL_IMAGE_UPLOAD);
            }

            banner = Banner.builder()
                    .startDate(reqDto.getBannerInfo().getStartDate())
                    .endDate(reqDto.getBannerInfo().getEndDate())
                    .fileUrl(reqDto.getBannerInfo().getImageUrl())
                    .build();
        }

        // 2. Notice 객체 빌드
        Notice notice = Notice.builder()
                .user(currentUser)
                .noticeStatus(reqDto.getNoticeStatus())
                .postStatus(reqDto.getPostStatus())
                .title(reqDto.getTitle())
                .content(reqDto.getContent())
                .bannerRegistered(reqDto.isBannerRegistered())
                .banner(banner)
                .build();

        // CTA 버튼 생성 추가
        reqDto.getButtons().forEach(dto -> {
            buttonRepository.save(Button.builder()
                    .notice(notice)
                    .text(dto.getText())
                    .position(dto.getPosition())
                    .link(dto.getLink())
                    .target(dto.getTarget())
                    .build());
        });

        // 3. 파일 존재 여부 확인 및 파일 추가 (루프 통합)
        if (reqDto.getFileUrls() != null && !reqDto.getFileUrls().isEmpty()) {
            for (String fileUrl : reqDto.getFileUrls()) {
                String key = extractKeyFromUrl(fileUrl);
                if (!s3Service.doesObjectExist(key)) {
                    throw new CustomException(ErrorCode.FAIL_FILE_UPLOAD);
                }

                notice.addFile(NoticeFile.builder()
                        .fileUrl(fileUrl)
                        .fileName(extractFileName(key))
                        .build());
            }
        }

        noticeRepository.save(notice);
        saveActionLogs(currentUser, notice, reqDto);

        return new NoticeResDto(notice.getId(), "공지사항이 정상적으로 작성되었습니다.");
    }

    @Transactional
    public UpdateNoticeResDto updateNotice(Long noticeId, UpdateNoticeReqDto reqDto) {
        Notice notice = getNotice(noticeId);
        Users currentUser = UserUtil.getCurrentUser();

        // 1. 파일 개수 검증 (현재 - 삭제 + 추가)
        int currentCount = notice.getFiles().size();
        int deleteCount = reqDto.getDeleteFileIds() == null ? 0 : reqDto.getDeleteFileIds().size();
        int addingCount = reqDto.getAddFiles() == null ? 0 : reqDto.getAddFiles().size();
        validateFileCount(currentCount - deleteCount, addingCount);

        // 2. 파일 삭제 (S3 및 DB 연관관계 제거)
        if (deleteCount > 0) {
            List<NoticeFile> filesToDelete = noticeFileRepository.findAllById(reqDto.getDeleteFileIds());
            filesToDelete.forEach(file -> {
                s3Service.deleteObject(extractKeyFromUrl(file.getFileUrl()));
                notice.removeFile(file);
            });
        }

        // 3. 파일 추가 (S3 존재 확인 및 연관관계 추가)
        if (addingCount > 0) {
            for (String url : reqDto.getAddFiles()) {
                String key = extractKeyFromUrl(url);
                if (!s3Service.doesObjectExist(key)) {
                    throw new CustomException(ErrorCode.FAIL_FILE_UPLOAD);
                }
                notice.addFile(NoticeFile.builder()
                        .fileUrl(url)
                        .fileName(extractFileName(key))
                        .build());
            }
        }

        // 4. 배너 정보 업데이트 및 S3 검증
        updateBannerInfo(notice, reqDto.getBannerInfo());

        // 5. 상태 변경 로그 기록
        if (!reqDto.getPostStatus().equals(notice.getPostStatus())) {
            saveStatusChangeLog(currentUser, noticeId, reqDto.getPostStatus());
        }

        // 6. 엔티티 정보 업데이트 (Dirty Checking)
        notice.update(reqDto);

        // 수정 완료 로그
        saveUpdateLog(currentUser, noticeId);

        return new UpdateNoticeResDto(notice.getId(), "공지사항이 성공적으로 수정되었습니다.");
    }

    private void saveActionLogs(Users user, Notice notice, CreateNoticeReqDto reqDto) {
        // 첨부파일 로그
        if (reqDto.getFileUrls() != null && !reqDto.getFileUrls().isEmpty()) {
            adminActionLogRepository.save(AdminActionLog.builder()
                    .actorType(AdminActorType.ADMIN)
                    .admin(user)
                    .pageType(AdminPageType.CONTENT_MANAGEMENT)
                    .actionType(AdminActionType.UPLOAD)
                    .targetType(AdminTargetType.FILE)
                    .resultType(AdminActionResult.SUCCESS)
                    .description(String.format("[업로드] %d번 공지 첨부파일 업로드", notice.getId()))
                    .build());
        }

        // 공지 생성 로그
        adminActionLogRepository.save(AdminActionLog.builder()
                .actorType(AdminActorType.ADMIN)
                .admin(user)
                .pageType(AdminPageType.CONTENT_MANAGEMENT)
                .actionType(AdminActionType.CREATE)
                .targetType(AdminTargetType.NOTICE)
                .resultType(AdminActionResult.SUCCESS)
                .description(String.format("[생성] 신규 공지 %s 등록", reqDto.getTitle()))
                .build());
    }

    private void saveStatusChangeLog(Users user, Long noticeId, PostStatus newStatus) {
        String statusDescription = newStatus.equals(PostStatus.ACTIVE) ? "중단->게시" : "게시->중단";

        adminActionLogRepository.save(AdminActionLog.builder()
                .actorType(AdminActorType.ADMIN)
                .admin(user)
                .pageType(AdminPageType.CONTENT_MANAGEMENT)
                .actionType(AdminActionType.UPDATE)
                .targetType(AdminTargetType.NOTICE)
                .resultType(AdminActionResult.SUCCESS)
                .description(String.format("[상태 변경] %d번 공지 %s", noticeId, statusDescription))
                .build()
        );
    }

    private void saveUpdateLog(Users user, Long noticeId) {
        adminActionLogRepository.save(AdminActionLog.builder()
                .actorType(AdminActorType.ADMIN)
                .admin(user)
                .pageType(AdminPageType.CONTENT_MANAGEMENT)
                .actionType(AdminActionType.UPDATE)
                .targetType(AdminTargetType.NOTICE)
                .resultType(AdminActionResult.SUCCESS)
                .description(String.format("[수정] %d번 공지 본문 내용 수정", noticeId))
                .build()
        );
    }

    private void updateBannerInfo(Notice notice, UpdateNoticeReqDto.BannerReqDto bannerReq) {
        if (bannerReq == null || bannerReq.getImageUrl() == null) return;

        String newImageUrl = bannerReq.getImageUrl();
        Banner banner = notice.getBanner();

        // 기존 배너가 없거나 이미지가 변경된 경우 S3 존재 확인
        if (banner == null || !newImageUrl.equals(banner.getFileUrl())) {
            String key = extractKeyFromUrl(newImageUrl);
            if (!s3Service.doesObjectExist(key)) {
                throw new CustomException(ErrorCode.FAIL_IMAGE_UPLOAD);
            }

            // 기존 배너 파일이 있었다면 S3에서 삭제 (선택 사항)
            if (banner != null && banner.getFileUrl() != null) {
                s3Service.deleteObject(extractKeyFromUrl(banner.getFileUrl()));
            }
        }
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
