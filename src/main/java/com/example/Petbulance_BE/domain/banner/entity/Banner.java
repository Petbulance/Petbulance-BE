package com.example.Petbulance_BE.domain.banner.entity;

import com.example.Petbulance_BE.domain.notice.dto.request.UpdateNoticeReqDto;
import com.example.Petbulance_BE.global.common.mapped.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "banners")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banner extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startDate;
    private  LocalDate endDate;

    @Builder.Default
    private String fileUrl = null;

    public void update(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void updateFileUrl(String key) {
        this.fileUrl = key;
    }

    public void updateBanner(UpdateNoticeReqDto.BannerReqDto bannerInfo) {
        this.startDate = bannerInfo.getStartDate();
        this.endDate = bannerInfo.getEndDate();
        this.fileUrl = bannerInfo.getImageUrl();
    }
}
