package com.example.Petbulance_BE.domain.comment.repository;

import com.example.Petbulance_BE.domain.comment.dto.response.PostCommentListResDto;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.user.entity.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PostCommentRepositoryCustom {
    Slice<PostCommentListResDto> findPostCommentByPostId(Post post, Long lastParentCommentId, Long lastCommentId, Pageable pageable, boolean isPostAuthor, Users currentUser);
}
