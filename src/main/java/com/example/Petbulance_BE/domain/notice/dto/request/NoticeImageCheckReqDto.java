package com.example.Petbulance_BE.domain.notice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeImageCheckReqDto {
    private Long noticeId;

    private List<String> keys = new ArrayList<>();
}
