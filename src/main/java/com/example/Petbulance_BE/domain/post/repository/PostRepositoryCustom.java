package com.example.Petbulance_BE.domain.post.repository;

import com.example.Petbulance_BE.domain.post.dto.response.DetailPostResDto;
import com.example.Petbulance_BE.domain.post.dto.response.PagingMyPostListResDto;
import com.example.Petbulance_BE.domain.post.dto.response.PagingPostSearchListResDto;
import com.example.Petbulance_BE.domain.post.dto.response.PostListResDto;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.type.Topic;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface PostRepositoryCustom {
    int fetchLikeCount(Long postId);
    int fetchCommentCount(Long postId);
    boolean fetchLikedByUser(Users currentUser, Long postId);
    Slice<PostListResDto> findPostList(AnimalType type, Topic topic, String sort, Long lastPostId, Integer pageSize);
    PagingPostSearchListResDto findPostSearchList(AnimalType type, Topic topic, String sort, Long lastPostId, Integer pageSize, String searchKeyword, String searchScope);
    PagingMyPostListResDto findMyPostList(Users currentUser, String keyword, Long lastPostId, Pageable pageable);
}
