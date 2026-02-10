package com.example.Petbulance_BE.domain.notice.dto.response;

import lombok.*;

import java.net.URL;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeResDto {
    private Long noticeId;
    private String message;
}
