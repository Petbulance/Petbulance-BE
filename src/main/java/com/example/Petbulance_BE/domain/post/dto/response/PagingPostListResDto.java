package com.example.Petbulance_BE.domain.post.dto.response;

import org.springframework.data.domain.Slice;

import java.util.List;

public class PagingPostListResDto {
    private List<PostListResDto> content;
    private boolean hasNext;

    public PagingPostListResDto(Slice<PostListResDto> slice) {
        this.content = slice.getContent();
        this.hasNext = slice.hasNext();
    }
}
