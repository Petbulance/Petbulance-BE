package com.example.Petbulance_BE.domain.post.service;

import com.example.Petbulance_BE.domain.post.dto.PostLikeDto;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.entity.PostLike;
import com.example.Petbulance_BE.domain.post.repository.PostLikeCountRepository;
import com.example.Petbulance_BE.domain.post.repository.PostLikeRepository;
import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.util.UserUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PostLikeServiceTest {

    @InjectMocks
    PostLikeService postLikeService;

    @Mock
    PostRepository postRepository;

    @Mock
    PostLikeRepository postLikeRepository;

    @Mock
    PostLikeCountRepository postLikeCountRepository;

    @Mock
    UserUtil userUtil;

    @Test
    @DisplayName("좋아요 내역이 없는 게시글에 좋아요를 생성했을 때, 해당 게시글의 좋아요 수 1개 검증")
    void createLikeWhenNoExistingLike_thenLikeCountIsOne() {
        // given
        Post post = mock(Post.class);
        Users user = mock(Users.class);
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        mockStatic(UserUtil.class).when(UserUtil::getCurrentUser).thenReturn(user);
        given(postLikeRepository.existsByPostIdAndUser(1L, user)).willReturn(false);
        given(postLikeCountRepository.increase(1L)).willReturn(1);
        given(postLikeCountRepository.getCountByPostId(1L)).willReturn(1L);

        // when
        PostLikeDto result = postLikeService.postLike(1L);

        // then
        assertThat(result.getLikeCount()).isEqualTo(1L);
        assertThat(result.isLiked()).isTrue();
    }

    @Test
    @DisplayName("좋아요 수가 3개인 게시글에 좋아요를 생성했을 때, 해당 게시글의 좋아요 수 4개 검증")
    void createLikeWhenExistingThreeLikes_thenLikeCountIsFour() {
        // given
        Post post = mock(Post.class);
        Users user = mock(Users.class);
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        mockStatic(UserUtil.class).when(UserUtil::getCurrentUser).thenReturn(user);
        given(postLikeRepository.existsByPostIdAndUser(1L, user)).willReturn(false);
        given(postLikeCountRepository.increase(1L)).willReturn(1);
        given(postLikeCountRepository.getCountByPostId(1L)).willReturn(4L);

        // when
        PostLikeDto result = postLikeService.postLike(1L);

        // then
        assertThat(result.getLikeCount()).isEqualTo(4L);
    }

    @Test
    @DisplayName("이미 좋아요한 게시글에 중복 좋아요 요청을 보냈을 때 예외 발생 검증")
    void createLikeWhenAlreadyLiked_thenThrowsException() {
        // given
        Post post = mock(Post.class);
        Users user = mock(Users.class);
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        mockStatic(UserUtil.class).when(UserUtil::getCurrentUser).thenReturn(user);
        given(postLikeRepository.existsByPostIdAndUser(1L, user)).willReturn(true);

        // when
        CustomException ex = assertThrows(CustomException.class, () -> postLikeService.postLike(1L));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ALREADY_LIKED);
    }

    @Test
    @DisplayName("좋아요 수가 1개인 게시글에 좋아요를 취소했을 때, 해당 게시글의 좋아요 수 0개 검증")
    void cancelLikeWhenLikeCountOne_thenLikeCountIsZero() {
        // given
        Post post = mock(Post.class);
        Users user = mock(Users.class);
        PostLike postLike = mock(PostLike.class);

        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        mockStatic(UserUtil.class).when(UserUtil::getCurrentUser).thenReturn(user);
        given(postLikeRepository.findByPostAndUser(post, user)).willReturn(Optional.of(postLike));
        given(postLikeCountRepository.decrease(1L)).willReturn(1);
        given(postLikeCountRepository.getCountByPostId(1L)).willReturn(0L);

        // when
        PostLikeDto result = postLikeService.postUnlike(1L);

        // then
        assertThat(result.getLikeCount()).isEqualTo(0L);
        assertThat(result.isLiked()).isFalse();
    }

    @Test
    @DisplayName("좋아요 수가 3개인 게시글에 좋아요를 취소했을 때, 해당 게시글의 좋아요 수 2개 검증")
    void cancelLikeWhenLikeCountThree_thenLikeCountIsTwo() {
        // given
        Post post = mock(Post.class);
        Users user = mock(Users.class);
        PostLike postLike = mock(PostLike.class);

        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        mockStatic(UserUtil.class).when(UserUtil::getCurrentUser).thenReturn(user);
        given(postLikeRepository.findByPostAndUser(post, user)).willReturn(Optional.of(postLike));
        given(postLikeCountRepository.decrease(1L)).willReturn(1);
        given(postLikeCountRepository.getCountByPostId(1L)).willReturn(2L);

        // when
        PostLikeDto result = postLikeService.postUnlike(1L);

        // then
        assertThat(result.getLikeCount()).isEqualTo(2L);
    }

    @Test
    @DisplayName("좋아요 내역이 없는 게시글에 좋아요 취소 요청을 보냈을 때 예외 발생 검증")
    void cancelLikeWhenNoExistingLike_thenThrowsException() {
        // given
        Post post = mock(Post.class);
        Users user = mock(Users.class);
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        mockStatic(UserUtil.class).when(UserUtil::getCurrentUser).thenReturn(user);
        given(postLikeRepository.findByPostAndUser(post, user)).willReturn(Optional.empty());

        // when
        CustomException ex = assertThrows(CustomException.class, () -> postLikeService.postUnlike(1L));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.LIKE_NOT_FOUND);
    }
}
