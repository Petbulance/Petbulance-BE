package com.example.Petbulance_BE.domain.notice.dto.response;

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
    private LocalDateTime createdAt;
}
