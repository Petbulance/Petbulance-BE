package com.example.Petbulance_BE.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagingPostSearchListResDto {
    private List<PostSearchListResDto> content;
    private boolean hasNext;
    private Long totalPostCount;

    public PagingPostSearchListResDto(Slice<PostSearchListResDto> slice, Long totalPostCount) {
        this.content = slice.getContent();
        this.hasNext = slice.hasNext();
        this.totalPostCount = totalPostCount;
    }
}
