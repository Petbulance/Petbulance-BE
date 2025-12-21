package com.example.Petbulance_BE.domain.notice.dto.response;

import com.example.Petbulance_BE.domain.notice.type.NoticeStatus;
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
    private String createdAt;

    public AdminNoticeListResDto(Long noticeId, String title, NoticeStatus noticeStatus, LocalDateTime createdAt) {
        this.noticeId = noticeId;
        this.title = title;
        this.noticeStatus = noticeStatus;
        this.createdAt = TimeUtil.formatCreatedAt(createdAt);
    }

}
