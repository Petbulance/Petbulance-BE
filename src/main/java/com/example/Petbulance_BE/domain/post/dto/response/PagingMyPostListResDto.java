package com.example.Petbulance_BE.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagingMyPostListResDto {
    private List<MyPostListResDto> content;
    private Long lastPostId;
    private boolean hasNext;

    public PagingMyPostListResDto(List<MyPostListResDto> content, boolean hasNext) {
        this.content = content;
        this.hasNext = hasNext;

        if (content != null && !content.isEmpty()) {
            this.lastPostId = content.get(content.size() - 1).getPostId();
        } else {
            this.lastPostId = null;
        }
    }
}
