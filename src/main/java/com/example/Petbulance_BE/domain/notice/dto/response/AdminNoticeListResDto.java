package com.example.Petbulance_BE.domain.notice.dto.response;

import com.example.Petbulance_BE.domain.notice.type.NoticeStatus;
import com.example.Petbulance_BE.domain.notice.type.PostStatus;
import com.example.Petbulance_BE.global.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminNoticeListResDto {
    private Long noticeId;
    private String title;
    private NoticeStatus noticeStatus;
    private PostStatus postStatus;
    private String createdAt;
    private boolean bannerRegistered;

    public AdminNoticeListResDto(Long noticeId, String title, NoticeStatus noticeStatus, PostStatus postStatus, LocalDateTime createdAt, boolean bannerRegistered) {
        this.noticeId = noticeId;
        this.title = title;
        this.noticeStatus = noticeStatus;
        this.postStatus = postStatus;
        this.createdAt = TimeUtil.formatCreatedAt(createdAt);
        this.bannerRegistered = bannerRegistered;
    }

}
