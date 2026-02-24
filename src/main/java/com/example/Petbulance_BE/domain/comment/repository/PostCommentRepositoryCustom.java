package com.example.Petbulance_BE.domain.comment.repository;

import com.example.Petbulance_BE.domain.comment.dto.response.PagingMyCommentListResDto;
import com.example.Petbulance_BE.domain.comment.dto.response.PostCommentListResDto;
import com.example.Petbulance_BE.domain.comment.dto.response.SearchPostCommentResDto;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.type.Topic;
import com.example.Petbulance_BE.domain.user.entity.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface PostCommentRepositoryCustom {
    Slice<PostCommentListResDto> findPostCommentByPost(Post post, Long lastParentCommentId, Long lastCommentId, Pageable pageable, boolean currentUserIsPostAuthor, Users currentUser);
    Slice<SearchPostCommentResDto> findSearchPostComment(String keyword, String searchScope, Long lastCommentId, Integer pageSize, List<Topic> topic, Long boardId);
    long countSearchPostComment(String keyword, String searchScope, List<Topic> categories, Long boardId);
    Slice<PostCommentListResDto> findPostCommentByPostForGuest(Post post, Long lastParentCommentId, Long lastCommentId, Pageable pageable);
    PagingMyCommentListResDto findMyCommentList(Users currentUser, String keyword, Long lastCommentId, Pageable pageable);
}
