package com.example.Petbulance_BE.domain.notice.dto.response;

import com.example.Petbulance_BE.global.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeListResDto {
    private Long noticeId;
    private boolean isImportant;
    private String title;
    private String content;
    private String createdAt;

    public NoticeListResDto(Long noticeId, boolean isImportant, String title, String content, LocalDateTime createdAt) {
        this.noticeId = noticeId;
        this.isImportant = isImportant;
        this.title = title;
        this.content = content;
        this.createdAt = TimeUtil.formatCreatedAt(createdAt);
    }
}
