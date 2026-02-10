package com.example.Petbulance_BE.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagingPostListResDto {
    private List<PostListResDto> content;
    private boolean hasNext;

    public PagingPostListResDto(Slice<PostListResDto> slice) {
        this.content = slice.getContent();
        this.hasNext = slice.hasNext();
    }
}
