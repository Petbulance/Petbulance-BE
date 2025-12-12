package com.example.Petbulance_BE.domain.post.dto.request;

import java.util.List;

public record BulkDeletePostReqDto(
        List<Long> postIds
) {}
