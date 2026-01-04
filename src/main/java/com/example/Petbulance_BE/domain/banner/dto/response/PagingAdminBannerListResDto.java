package com.example.Petbulance_BE.domain.banner.dto.response;

import com.example.Petbulance_BE.domain.report.dto.response.ReportListResDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PagingAdminBannerListResDto {
    private List<BannerListResDto> content;

    private int page;
    private int size;

    private int totalPages;
    private long totalElements;

    private boolean hasNext;
    private boolean hasPrev;
}
