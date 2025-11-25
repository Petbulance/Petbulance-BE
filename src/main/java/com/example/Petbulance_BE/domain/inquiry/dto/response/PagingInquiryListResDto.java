package com.example.Petbulance_BE.domain.inquiry.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagingInquiryListResDto {
    private List<InquiryListResDto> content;
    private boolean hasNext;
}
