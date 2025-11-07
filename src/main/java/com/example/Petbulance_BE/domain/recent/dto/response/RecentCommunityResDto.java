package com.example.Petbulance_BE.domain.recent.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentCommunityResDto implements Serializable {
    private String keywordId;
    private String keyword;
    private LocalDateTime createdAt;
}
