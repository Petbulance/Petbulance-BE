package com.example.Petbulance_BE.domain.banner.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BannerImageCheckReqDto {
    private Long bannerId;
    private String key;
}
