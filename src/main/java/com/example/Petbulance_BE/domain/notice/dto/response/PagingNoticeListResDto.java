package com.example.Petbulance_BE.domain.notice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagingNoticeListResDto {
    private List<NoticeListResDto> content;
    private boolean hasNext;
}
