package com.example.Petbulance_BE.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeletePostResDto {
    private Long postId;
    private Long boardId;
    private boolean deleted;
    private boolean hidden;
    private LocalDateTime deletedAt;
}
