package com.example.Petbulance_BE.domain.notice.service;

import com.example.Petbulance_BE.domain.notice.dto.request.CreateNoticeReqDto;
import com.example.Petbulance_BE.domain.notice.dto.request.NoticeImageCheckReqDto;
import com.example.Petbulance_BE.domain.notice.dto.request.UpdateNoticeReqDto;
import com.example.Petbulance_BE.domain.notice.dto.response.NoticeResDto;
import com.example.Petbulance_BE.domain.notice.dto.response.DetailNoticeResDto;
import com.example.Petbulance_BE.domain.notice.dto.response.PagingAdminNoticeListResDto;
import com.example.Petbulance_BE.domain.notice.dto.response.PagingNoticeListResDto;
import com.example.Petbulance_BE.domain.notice.entity.Notice;
import com.example.Petbulance_BE.domain.notice.entity.NoticeFile;
import com.example.Petbulance_BE.domain.notice.repository.NoticeFileRepository;
import com.example.Petbulance_BE.domain.notice.repository.NoticeRepository;
import com.example.Petbulance_BE.domain.review.dto.req.ReviewSaveReqDto;
import com.example.Petbulance_BE.domain.review.dto.res.ReviewSaveResDto;
import com.example.Petbulance_BE.domain.review.entity.UserReviewImage;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.common.s3.S3Service;
import com.example.Petbulance_BE.global.util.UserUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;
    private final S3Service s3Service;

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

        List<CreateNoticeReqDto.NoticeFileReqDto> files = reqDto.getFiles();

        if(files.size()>5) throw new CustomException(ErrorCode.MAX_FILE_COUNT_EXCEEDED);

        Notice notice = Notice.builder()
                .noticeStatus(reqDto.getNoticeStatus())
                .postStatus(reqDto.getPostStatus())
                .title(reqDto.getTitle())
                .content(reqDto.getContent())
                .postStartDate(reqDto.getStartDate())
                .postEndDate(reqDto.getEndDate())
                .build();

        List<NoticeResDto.UrlAndId> list = new LinkedList<>();
        for(CreateNoticeReqDto.NoticeFileReqDto file : files){

            String filename = file.getFilename();
            String contentType = file.getContentType();

            String key = "noticeImage/" + UUID.randomUUID() + "_" + filename;

            URL presignedPutUrl = s3Service.createPresignedPutUrl(key, contentType, 300);

            list.add(new NoticeResDto.UrlAndId(presignedPutUrl, key));
        }

        noticeRepository.save(notice);

        return new NoticeResDto(notice.getId(), "공지사항이 정상적으로 작성되었습니다.", list);
    }



    /*@Transactional
    public NoticeResDto updateNotice(Long noticeId, UpdateNoticeReqDto reqDto) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));

        notice.update(reqDto);

        if (reqDto.getDeleteFileIds() != null && !reqDto.getDeleteFileIds().isEmpty()) {

            List<NoticeFile> deleteTargets =
                    noticeFileRepository.findAllById(reqDto.getDeleteFileIds());

            deleteTargets.forEach(file -> {

                // URL → S3 key 변환 (현재 구조 기준)
                String fileUrl = file.getFileUrl();
                String key = fileUrl.substring(fileUrl.indexOf("notice/"));

                // 1) S3 삭제
                s3Service.deleteObject(key);

                // 2) 연관관계 제거(JPA orphanRemoval)
                notice.removeFile(file);
            });
        }

        int currentCount = notice.getFiles().size();
        int adding = (reqDto.getAddFiles() == null) ? 0 : reqDto.getAddFiles().size();

        if (currentCount + adding > 5) {
            throw new CustomException(ErrorCode.MAX_FILE_COUNT_EXCEEDED);
        }

        if (reqDto.getAddFiles() != null && !reqDto.getAddFiles().isEmpty()) {

            reqDto.getAddFiles().forEach(dto -> {

                NoticeFile file = NoticeFile.builder()
                        .fileUrl(dto.getFileUrl())
                        .fileName(dto.getFileName())
                        .fileType(dto.getFileType())
                        .build();

                notice.addFile(file);
            });
        }

        return new NoticeResDto("공지사항이 정상적으로 수정되었습니다.");
    }
*/


    @Transactional(readOnly = true)
    public PagingAdminNoticeListResDto adminNoticeList(int page, int size) {
        return noticeRepository.adminNoticeList(page, size);
    }

    public void noticeFileSaveCheckProcess(NoticeImageCheckReqDto reqDto) {
        Users currentUser = UserUtil.getCurrentUser();

        Long noticeId = reqDto.getNoticeId();
        List<String> keys = reqDto.getKeys();

        Notice notice = noticeRepository.findByIdAndUserId(noticeId, currentUser.getId());

        Boolean[] exists = new Boolean[keys.size()];
        Boolean allExists = true;

        for(int i=0; i<exists.length; i++){
            boolean b = s3Service.doesObjectExist(keys.get(i));
            if(!b){
                allExists = false;
            }
            exists[i] = b;
        }

        if(allExists){ // 모두 다 업로드가 잘되었다면
            for(String key : keys){
                NoticeFile noticeFile = NoticeFile.builder()
                        .fileUrl(s3Service.getObject(key))
                        .notice(notice)
                        .fileName(key.split("_", 2)[1])
                        .build();

                noticeFileRepository.save(noticeFile);
            }
        }else{ // 업로드 실패시 공지 자체 삭제
            for(int i=0; i<exists.length; i++){
                if(exists[i]){
                    try {
                        s3Service.deleteObject(keys.get(i));
                    } catch (Exception e) {
                        log.warn("S3 삭제 실패 : {}", keys.get(i), e);
                    }
                }
            }
            noticeRepository.deleteById(noticeId);
            throw new CustomException(ErrorCode.FAIL_IMAGE_UPLOAD);
        }

    }
}
