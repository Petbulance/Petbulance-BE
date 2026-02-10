package com.example.Petbulance_BE.domain.inquiry.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagingAdminInquiryListResDto {
    private List<AdminInquiryListResDto> content;

    private int page;
    private int size;

    private int totalPages;
    private long totalElements;

    private boolean hasNext;
    private boolean hasPrev;
}
