package com.example.Petbulance_BE.domain.banner.dto.response;

import com.example.Petbulance_BE.domain.notice.dto.response.NoticeResDto;
import com.example.Petbulance_BE.domain.notice.type.NoticeStatus;
import com.example.Petbulance_BE.domain.notice.type.PostStatus;
import lombok.*;

import java.net.URL;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BannerResDto {
    private Long bannerId;
    private Long noticeId;
    private PostStatus postStatus;
    private NoticeStatus noticeStatus;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private UrlAndId url;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UrlAndId{
        private URL presignedUrl;
        private String saveId;
    }
}
