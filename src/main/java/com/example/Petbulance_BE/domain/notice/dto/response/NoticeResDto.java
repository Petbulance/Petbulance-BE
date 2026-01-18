package com.example.Petbulance_BE.domain.notice.dto.response;

import lombok.*;

import java.net.URL;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeResDto {
    private Long noticeId;
    private String message;
    private List<UrlAndId> urls;
    private BannerResInfo bannerInfo;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UrlAndId{
        private URL presignedUrl;
        private String saveId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BannerResInfo {
        private Long bannerId;
        private URL presignedUrl;
        private String saveId;
    }
}
