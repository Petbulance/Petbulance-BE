package com.example.Petbulance_BE.domain.post.dto.response;

import java.time.LocalDateTime;

public record BulkDeletePostItemDto(
        Long postId,
        Long boardId,
        boolean deleted,
        boolean hidden,
        LocalDateTime deletedAt
) {}

