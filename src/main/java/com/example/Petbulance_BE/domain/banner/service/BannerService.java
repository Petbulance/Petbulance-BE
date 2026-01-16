package com.example.Petbulance_BE.domain.banner.service;

import com.example.Petbulance_BE.domain.banner.dto.request.BannerImageCheckReqDto;
import com.example.Petbulance_BE.domain.banner.dto.request.CreateBannerReqDto;
import com.example.Petbulance_BE.domain.banner.dto.request.UpdateBannerReqDto;
import com.example.Petbulance_BE.domain.banner.dto.response.BannerResDto;
import com.example.Petbulance_BE.domain.banner.dto.response.PagingAdminBannerListResDto;
import com.example.Petbulance_BE.domain.banner.entity.Banner;
import com.example.Petbulance_BE.domain.banner.repository.BannerRepository;
import com.example.Petbulance_BE.domain.notice.entity.Notice;
import com.example.Petbulance_BE.domain.notice.entity.NoticeFile;
import com.example.Petbulance_BE.domain.notice.repository.NoticeRepository;
import com.example.Petbulance_BE.domain.notice.type.PostStatus;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.common.s3.S3Service;
import com.example.Petbulance_BE.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BannerService {
    private final NoticeRepository noticeRepository;
    private final BannerRepository bannerRepository;
    private final S3Service s3Service;

    @Transactional(readOnly = true)
    public PagingAdminBannerListResDto adminBannerList(int page, int size) {
        return null;
    }

    @Transactional
    public BannerResDto createBanner(CreateBannerReqDto reqDto) {

        checkBannerIsActiveCount(reqDto.getPostStatus());
        Users currentUser = UserUtil.getCurrentUser();

        Long noticeId = reqDto.getNoticeId();
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));

        String key = createFileKey(reqDto.getFile().getFilename());
        URL url = s3Service.createPresignedPutUrl(key, reqDto.getFile().getContentType(), 300);

        Banner banner = bannerRepository.save(Banner.builder()
                .notice(notice)
                .postStatus(reqDto.getPostStatus())
                .noticeStatus(reqDto.getNoticeStatus())
                .title(reqDto.getTitle())
                .startDate(reqDto.getStartDate())
                .endDate(reqDto.getEndDate())
                .users(currentUser)
                .build());

        return BannerResDto.builder()
                .bannerId(banner.getId())
                .noticeId(noticeId)
                .postStatus(banner.getPostStatus())
                .noticeStatus(banner.getNoticeStatus())
                .title(banner.getTitle())
                .startDate(banner.getStartDate())
                .endDate(banner.getEndDate())
                .url(BannerResDto.UrlAndId
                        .builder()
                        .presignedUrl(url)
                        .saveId(key)
                        .build())
                .build();
    }

    private void checkBannerIsActiveCount(PostStatus postStatus) {
        long bannerCount = 0;
        if (postStatus.equals(PostStatus.ACTIVE)) {
            bannerCount = bannerRepository.countByPostStatus(PostStatus.ACTIVE); // 현재 게시중인 배너가 이미 5개이면 예외
        }
        if (bannerCount >= 5) throw new CustomException(ErrorCode.MAX_BANNER_COUNT_EXCEEDED);
    }

    @Transactional
    public BannerResDto updateBanner(Long bannerId, UpdateBannerReqDto reqDto) {
        checkBannerIsActiveCount(reqDto.getPostStatus());

        Banner banner = getBanner(bannerId);

        Long noticeId = reqDto.getNoticeId();
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));

        String key = null;
        URL url = null;
        // 기존 이미지에서 변경
        if(StringUtils.hasText(reqDto.getFile().getFilename())) {
            key = createFileKey(reqDto.getFile().getFilename());
            url = s3Service.createPresignedPutUrl(key, reqDto.getFile().getContentType(), 300);
        }

        banner.update(reqDto.getNoticeStatus(), reqDto.getPostStatus(), notice, reqDto.getTitle(), reqDto.getStartDate(), reqDto.getEndDate());

        return BannerResDto.builder()
                .bannerId(banner.getId())
                .noticeId(noticeId)
                .postStatus(banner.getPostStatus())
                .noticeStatus(banner.getNoticeStatus())
                .title(banner.getTitle())
                .startDate(banner.getStartDate())
                .endDate(banner.getEndDate())
                .url(BannerResDto.UrlAndId
                        .builder()
                        .presignedUrl(url)
                        .saveId(key)
                        .build())
                .build();
    }

    private Banner getBanner(Long bannerId) {
        return bannerRepository.findById(bannerId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BANNER));
    }

    private String createFileKey(String filename) {
        return "bannerImage/" + UUID.randomUUID() + "_" + filename;
    }

    public void bannerFileSaveCheckProcess(BannerImageCheckReqDto reqDto) {
        Banner banner = getBanner(reqDto.getBannerId());

        try {
            saveBannerFilesOrThrow(banner, reqDto.getKey());
        } catch (CustomException e) {
            cleanupUploadedFiles(reqDto.getKey());
            if(banner.getFileUrl() == null) bannerRepository.delete(banner);
            throw e;
        }
    }

    private void cleanupUploadedFiles(String key) {
        try {
            s3Service.deleteObject(key);
        } catch (Exception e) {
            log.warn("S3 파일 삭제 실패: {}", key, e);
        }
    }

    private void saveBannerFilesOrThrow(Banner banner, String key) {
        if (!s3Service.doesObjectExist(key)) {
            throw new CustomException(ErrorCode.FAIL_FILE_UPLOAD);
        }
        banner.updateFileUrl(key);
    }
}
