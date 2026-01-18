package com.example.Petbulance_BE.domain.adminlog.dto.response;

import com.example.Petbulance_BE.domain.notice.dto.response.AdminNoticeListResDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagingAdminActionLogListResDto {
    private List<AdminActionLogListResDto> content;
    private int page;
    private int size;

    private int totalPages;
    private long totalElements;

    private boolean hasNext;
    private boolean hasPrev;
}
