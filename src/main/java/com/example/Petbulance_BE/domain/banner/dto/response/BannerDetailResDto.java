package com.example.Petbulance_BE.domain.banner.dto.response;

import com.example.Petbulance_BE.domain.notice.type.NoticeStatus;
import com.example.Petbulance_BE.domain.notice.type.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerDetailResDto {
    private Long bannerId;
    private String noticeTitle;
    private String writer;
    private PostStatus postStatus;
    private NoticeStatus noticeStatus;
    private String title;
    private LocalDate startDate;
    private  LocalDate endDate;
    private String fileUrl;

}
