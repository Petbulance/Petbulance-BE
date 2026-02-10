package com.example.Petbulance_BE.domain.comment.dto.response;

import java.util.List;

public record BulkDeleteCommentResDto(
        List<BulkDeleteCommentItemDto> deletedComments
) {}

