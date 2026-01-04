package com.example.Petbulance_BE.domain.banner.dto.response;

import com.example.Petbulance_BE.domain.notice.type.NoticeStatus;
import com.example.Petbulance_BE.domain.notice.type.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BannerListResDto {
    private Long bannerId;
    private NoticeStatus noticeStatus;
    private String title;
    private PostStatus postStatus;
}
