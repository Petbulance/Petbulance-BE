package com.example.Petbulance_BE.domain.review.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URL;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSaveResDto {

    private Long reviewId;

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
