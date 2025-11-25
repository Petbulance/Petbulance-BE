package com.example.Petbulance_BE.domain.comment.dto.response;

import java.time.LocalDateTime;

public record BulkDeleteCommentItemDto(
        Long commentId,
        Long postId,
        boolean deleted,
        boolean softDeleted,
        LocalDateTime deletedAt
) {}
