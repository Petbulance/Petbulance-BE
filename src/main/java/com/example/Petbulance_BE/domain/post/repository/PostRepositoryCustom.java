package com.example.Petbulance_BE.domain.post.repository;

import com.example.Petbulance_BE.domain.post.dto.response.InquiryPostResDto;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.user.entity.Users;

import java.util.Optional;

public interface PostRepositoryCustom {
    InquiryPostResDto findInquiryPost(Post post, boolean currentUserIsPostAuthor, Users currentUser, Long viewCount);
}
