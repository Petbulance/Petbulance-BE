package com.example.Petbulance_BE.domain.notice.dto.request;

import com.example.Petbulance_BE.domain.notice.type.NoticeStatus;
import com.example.Petbulance_BE.domain.notice.type.PostStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNoticeReqDto {
    private NoticeStatus noticeStatus;
    private PostStatus postStatus;

    @NotBlank(message = "제목(title)은 필수입니다.")
    private String title;
    @NotBlank(message = "내용(content)은 비워둘 수 없습니다.")
    private String content;

    private String fileUrl;
    private String fileName;
    private String fileType;

    private LocalDate startDate;
    private  LocalDate endDate;
}
