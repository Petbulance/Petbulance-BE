package com.example.Petbulance_BE.domain.notice.dto.request;

import com.example.Petbulance_BE.domain.notice.type.NoticeStatus;
import com.example.Petbulance_BE.domain.notice.type.PostStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNoticeReqDto {

    private NoticeStatus noticeStatus;
    private PostStatus postStatus;

    @NotBlank(message = "제목(title)은 필수입니다.")
    private String title;

    @NotBlank(message = "내용(content)은 비워둘 수 없습니다.")
    private String content;

    // 새로 추가될 파일들
    private List<String> addFiles;

    // 삭제할 파일 id 목록
    private List<Long> deleteFileIds;

    private boolean bannerRegistered; // 배너 설정 여부
    private BannerReqDto bannerInfo;

    private List<ButtonReqDto> buttons;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BannerReqDto {
        private LocalDate startDate;
        private LocalDate endDate;
        private String imageUrl;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ButtonReqDto {
        private Long buttonId;
        private String text;
        private String position;
        private String link;
        private String target;
    }

}

