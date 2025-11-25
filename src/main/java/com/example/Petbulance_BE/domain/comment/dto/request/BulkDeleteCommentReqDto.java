package com.example.Petbulance_BE.domain.comment.dto.request;

import java.util.List;

public record BulkDeleteCommentReqDto(
        List<Long> commentIds
) {}

