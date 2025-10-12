package com.example.Petbulance_BE.domain.post.service;

import com.example.Petbulance_BE.domain.post.dto.PostLikeDto;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.entity.PostLike;
import com.example.Petbulance_BE.domain.post.entity.PostLikeCount;
import com.example.Petbulance_BE.domain.post.repository.PostLikeCountRepository;
import com.example.Petbulance_BE.domain.post.repository.PostLikeRepository;
import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.util.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostLikeService {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostLikeCountRepository postLikeCountRepository;


    public PostLikeDto postLike(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Users currentUser = UserUtil.getCurrentUser();

        // 이미 좋아요한 경우 예외
        if (postLikeRepository.existsByPostIdAndUser(postId, currentUser)) {
            throw new CustomException(ErrorCode.ALREADY_LIKED);
        }

        // 좋아요 생성
        postLikeRepository.save(PostLike.builder()
                .post(post)
                .user(currentUser)
                .build());

        // 좋아요 수 증가
        int result = postLikeCountRepository.increase(postId);
        if (result == 0) {
            try {
                postLikeCountRepository.save(
                        PostLikeCount.builder()
                                .postId(postId)
                                .postLikeCount(1L)
                                .build()
                );
            } catch (Exception ignored) {}
        }

        // 최신 좋아요 수 조회
        long likeCount = Optional.ofNullable(
                postLikeCountRepository.getCountByPostId(postId)
        ).orElse(1L);

        return PostLikeDto.builder()
                .postId(postId)
                .liked(true)
                .likeCount(likeCount)
                .build();
    }

    public PostLikeDto postUnlike(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        Users currentUser = UserUtil.getCurrentUser();

        PostLike postLike = postLikeRepository.findByPostAndUser(post, currentUser)
                .orElseThrow(() -> new CustomException(ErrorCode.LIKE_NOT_FOUND));

        postLikeRepository.delete(postLike);

        int result = postLikeCountRepository.decrease(postId);
        if (result == 0) {
            // update된 행이 없으면 → 0으로 초기화 (이 게시글에 count row가 없는 경우)
            postLikeCountRepository.save(
                    PostLikeCount.builder()
                            .postId(postId)
                            .postLikeCount(0L)
                            .build()
            );
        }

        long likeCount = Optional.ofNullable(
                postLikeCountRepository.getCountByPostId(postId)
        ).orElse(0L);


        return PostLikeDto.builder()
                .postId(postId)
                .liked(false)
                .likeCount(likeCount)
                .build();
    }

}
