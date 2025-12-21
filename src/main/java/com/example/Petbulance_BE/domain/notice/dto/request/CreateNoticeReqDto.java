package com.example.Petbulance_BE.domain.notice.dto.request;

import com.example.Petbulance_BE.domain.notice.type.NoticeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateNoticeReqDto {
    private NoticeStatus noticeStatus;
    private String title;
    private String content;
    private String fileUrl;
    private String fileName;
    private String fileType;
}
