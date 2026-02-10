package com.example.Petbulance_BE.domain.post.dto.response;

import java.util.List;

public record BulkDeletePostResDto(
        List<BulkDeletePostItemDto> deletedPosts
) {}
