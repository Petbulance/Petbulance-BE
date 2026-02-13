package com.example.Petbulance_BE.domain.post.repository;

import com.example.Petbulance_BE.domain.post.dto.response.DetailPostResDto;
import com.example.Petbulance_BE.domain.post.dto.response.PagingMyPostListResDto;
import com.example.Petbulance_BE.domain.post.dto.response.PagingPostSearchListResDto;
import com.example.Petbulance_BE.domain.post.dto.response.PostListResDto;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.type.Category;
import com.example.Petbulance_BE.domain.user.entity.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface PostRepositoryCustom {
    DetailPostResDto findInquiryPost(Post post, boolean currentUserIsPostAuthor, Users currentUser, Long viewCount);
    int fetchLikeCount(Long postId);
    int fetchCommentCount(Long postId);
    boolean fetchLikedByUser(Users currentUser, Long postId);
    Slice<PostListResDto> findPostList(Long boardId, Category c, String sort, Long lastPostId, Integer pageSize);
    PagingPostSearchListResDto findPostSearchList(Long boardId, List<Category> category, String sort, Long lastPostId, Integer pageSize, String searchKeyword, String searchScope);
    PagingMyPostListResDto findMyPostList(Users currentUser, String keyword, Long lastPostId, Pageable pageable);
}
