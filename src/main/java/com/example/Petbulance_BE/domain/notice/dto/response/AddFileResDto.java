package com.example.Petbulance_BE.domain.notice.dto.response;

import lombok.*;

import java.net.URL;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddFileResDto {
    private List<UrlAndId> urls;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UrlAndId{
        private URL presignedUrl;
        private String saveId;

    }
}
