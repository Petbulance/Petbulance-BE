package com.example.Petbulance_BE.domain.notice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNoticeResDto {
    private Long noticeId;
    private String message;
}
