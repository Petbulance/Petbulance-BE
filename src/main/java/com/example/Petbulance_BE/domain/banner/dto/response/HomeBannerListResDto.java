package com.example.Petbulance_BE.domain.banner.dto.response;

import com.example.Petbulance_BE.domain.notice.type.NoticeStatus;
import com.example.Petbulance_BE.domain.notice.type.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeBannerListResDto {
    private Long bannerId;
    private LocalDate startDate;
    private LocalDate endDate;
    private PostStatus postStatus;
    private NoticeStatus noticeStatus;
    private Long noticeId;
    private String title;
    private String fileUrl;
}
