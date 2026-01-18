package com.example.Petbulance_BE.domain.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPresignResDto {
    private List<UrlInfo> uploadedFiles;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UrlInfo {
        private String preSignedUrl; // S3 업로드용 URL
        private String imageUrl;     // 나중에 DB 저장 및 조회 시 사용할 URL (또는 Key)
    }
}
