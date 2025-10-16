package com.example.Petbulance_BE.domain.comment.service;

import com.example.Petbulance_BE.domain.comment.dto.request.UpdatePostCommentReqDto;
import com.example.Petbulance_BE.domain.comment.entity.PostComment;
import com.example.Petbulance_BE.domain.comment.entity.PostCommentCount;
import com.example.Petbulance_BE.domain.comment.repository.PostCommentCountRepository;
import com.example.Petbulance_BE.domain.comment.repository.PostCommentRepository;
import com.example.Petbulance_BE.domain.post.dto.request.CreatePostCommentReqDto;
import com.example.Petbulance_BE.domain.comment.dto.response.PostCommentResDto;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.entity.PostLikeCount;
import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.util.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class PostCommentService {
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final UsersJpaRepository usersJpaRepository;
    private final PostCommentCountRepository postCommentCountRepository;

    @Transactional
    public PostCommentResDto createPostComment(Long postId, CreatePostCommentReqDto dto) {
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new CustomException(ErrorCode.EMPTY_COMMENT_CONTENT);
        }

        Post post = findPostById(postId);
        PostComment parentComment = findParentComment(dto.getParentId());
        Users mentionedUser = findMentionedUser(dto.getMentionUserNickname());

        PostComment saved = postCommentRepository.save(
                PostComment.builder()
                        .post(post)
                        .user(UserUtil.getCurrentUser())
                        .content(dto.getContent())
                        .parent(parentComment)
                        .mentionUser(mentionedUser)
                        .isSecret(dto.getIsSecret())
                        .imageUrl(dto.getImageUrl())
                        .build()
        );

        // 댓글 수 증가
        int result = postCommentCountRepository.increase(postId);
        if(result == 0) {
            try {
                postCommentCountRepository.save(
                        PostCommentCount.builder()
                                .postId(postId)
                                .postCommentCount(1L)
                                .build()
                );
            } catch (Exception ignored) {}
        }
        return PostCommentResDto.of(saved);
    }

    public PostCommentResDto updatePostComment(Long commentId, UpdatePostCommentReqDto dto) {
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new CustomException(ErrorCode.EMPTY_COMMENT_CONTENT);
        }
        PostComment postComment = findPostCommentById(commentId);
        Users mentionedUser = findMentionedUser(dto.getMentionUserNickname());

        postComment.update(dto, mentionedUser);
        return PostCommentResDto.of(postComment);
    }

    private PostComment findPostCommentById(Long commentId) {
        return postCommentRepository.findById(commentId).orElseThrow(
                () -> new CustomException(ErrorCode.COMMENT_NOT_FOUND)
        );
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    private PostComment findParentComment(Long parentId) {
        if (parentId == null) return null;
        return postCommentRepository.findById(parentId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_PARENT_COMMENT));
    }

    private Users findMentionedUser(String nickname) {
        if (nickname == null || nickname.isBlank()) return null;
        return usersJpaRepository.findByNickname(nickname)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_MENTION_USER));
    }

}
