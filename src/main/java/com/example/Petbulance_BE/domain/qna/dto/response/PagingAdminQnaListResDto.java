package com.example.Petbulance_BE.domain.qna.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagingAdminQnaListResDto {
    private List<AdminQnaListResDto> content;

    private int page;
    private int size;

    private int totalPages;
    private long totalElements;

    private boolean hasNext;
    private boolean hasPrev;
}
