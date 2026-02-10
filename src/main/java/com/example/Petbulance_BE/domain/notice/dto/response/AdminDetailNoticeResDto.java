package com.example.Petbulance_BE.domain.notice.dto.response;

import com.example.Petbulance_BE.domain.notice.type.NoticeStatus;
import com.example.Petbulance_BE.domain.notice.type.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDetailNoticeResDto {
    private PostStatus postStatus;
    private NoticeStatus noticeStatus;
    private String title;
    private String content;
    private List<FileResDto> files;
    private BannerInfoResDto bannerInfo;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileResDto {
        private Long fileId;
        private String fileUrl;
        private String fileName;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BannerInfoResDto {
        private LocalDate startDate;
        private LocalDate endDate;
        private String imageUrl;
    }
}
