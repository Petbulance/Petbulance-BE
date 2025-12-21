package com.example.Petbulance_BE.domain.notice.dto.request;

import com.example.Petbulance_BE.domain.notice.entity.NoticeFile;
import com.example.Petbulance_BE.domain.notice.type.NoticeStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
