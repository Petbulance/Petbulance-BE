package com.example.Petbulance_BE.domain.banner.dto.request;

import com.example.Petbulance_BE.domain.banner.type.BannerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBannerReqDto {
    private Long bannerId;
    private String title;
    //private BannerStatus status;
    // 파일 ?? 이미지?
    private String content;
}
