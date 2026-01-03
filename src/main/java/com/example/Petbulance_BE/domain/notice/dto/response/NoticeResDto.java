package com.example.Petbulance_BE.domain.notice.dto.response;

import com.example.Petbulance_BE.domain.review.dto.res.ReviewSaveResDto;
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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UrlAndId{
        private URL presignedUrl;
        private String saveId;

    }
}
