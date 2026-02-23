package com.example.Petbulance_BE.domain.notice.dto.response;

import com.example.Petbulance_BE.domain.notice.entity.Button;
import com.example.Petbulance_BE.domain.notice.type.NoticeStatus;
import com.example.Petbulance_BE.domain.notice.type.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
    private List<ButtonDto> buttons;

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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ButtonDto {
        private Long buttonId;
        private String text;
        private String position;
        private String link;
        private String target;

        public static DetailNoticeResDto.ButtonDto from(Button b) {
            return DetailNoticeResDto.ButtonDto.builder()
                    .buttonId(b.getId())
                    .text(b.getText())
                    .position(b.getPosition())
                    .link(b.getLink())
                    .target(b.getTarget())
                    .build();
        }
    }
}
