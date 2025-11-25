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
import org.mockito.MockedStatic;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class PostLikeServiceTest {

    @InjectMocks
    PostLikeService postLikeService;

    @Mock
    PostRepository postRepository;

    @Mock
    PostLikeRepository postLikeRepository;

    @Mock
    PostLikeCountRepository postLikeCountRepository;


    @Test
    @DisplayName("좋아요 내역이 없는 게시글에 좋아요 생성 시, 좋아요 수가 0→1 증가한다")
    void createLikeWhenNoExistingLike_thenLikeCountIsOne() {
        // given
        Post post = new Post();
        Users user = new Users();

        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        AtomicLong likeCount = new AtomicLong(0);

        given(postLikeRepository.existsByPostIdAndUser(1L, user)).willReturn(false);

        willAnswer(inv -> { likeCount.incrementAndGet(); return 1; })
                .given(postLikeCountRepository).increase(1L);
        given(postLikeCountRepository.getCountByPostId(1L))
                .willAnswer(inv -> likeCount.get());

        try (MockedStatic<UserUtil> mocked = mockStatic(UserUtil.class)) {
            mocked.when(UserUtil::getCurrentUser).thenReturn(user);

            // when
            PostLikeDto result = postLikeService.postLike(1L);

            // then
            assertThat(result.getLikeCount()).isEqualTo(1L);
            assertThat(result.isLiked()).isTrue();

            verify(postLikeRepository, times(1)).existsByPostIdAndUser(1L, user);
            verify(postLikeCountRepository, times(1)).increase(1L);
            verify(postLikeCountRepository, times(1)).getCountByPostId(1L);
            verifyNoMoreInteractions(postLikeCountRepository);
        }
    }

    @Test
    @DisplayName("좋아요 수가 3인 게시글에 좋아요 생성 시, 3→4로 증가한다")
    void createLikeWhenExistingThreeLikes_thenLikeCountIsFour() {
        // given
        Post post = new Post();
        Users user = new Users();

        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        AtomicLong likeCount = new AtomicLong(3);
        given(postLikeRepository.existsByPostIdAndUser(1L, user)).willReturn(false);

        willAnswer(inv -> { likeCount.incrementAndGet(); return 1; })
                .given(postLikeCountRepository).increase(1L);
        given(postLikeCountRepository.getCountByPostId(1L))
                .willAnswer(inv -> likeCount.get());

        try (MockedStatic<UserUtil> mocked = mockStatic(UserUtil.class)) {
            mocked.when(UserUtil::getCurrentUser).thenReturn(user);

            // when
            PostLikeDto result = postLikeService.postLike(1L);

            // then
            assertThat(result.getLikeCount()).isEqualTo(4L);
            assertThat(result.isLiked()).isTrue();

            verify(postLikeCountRepository, times(1)).increase(1L);
            verify(postLikeCountRepository, times(1)).getCountByPostId(1L);
        }
    }

    @Test
    @DisplayName("이미 좋아요한 게시글에 중복 좋아요 요청 시 예외 발생 및 increase() 호출 안 됨")
    void createLikeWhenAlreadyLiked_thenThrowsException() {
        // given
        Post post = new Post();
        Users user = new Users();

        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(postLikeRepository.existsByPostIdAndUser(1L, user)).willReturn(true);

        try (MockedStatic<UserUtil> mocked = mockStatic(UserUtil.class)) {
            mocked.when(UserUtil::getCurrentUser).thenReturn(user);

            // when
            CustomException ex = assertThrows(CustomException.class,
                    () -> postLikeService.postLike(1L));

            // then
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.ALREADY_LIKED);
            verify(postLikeCountRepository, never()).increase(1L);
            verify(postLikeCountRepository, never()).getCountByPostId(1L);
        }
    }

    @Test
    @DisplayName("좋아요 수가 1인 게시글에서 취소 시, 1→0으로 감소한다")
    void cancelLikeWhenLikeCountOne_thenLikeCountIsZero() {
        // given
        Post post = new Post();
        Users user = new Users();
        PostLike postLike = new PostLike();

        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(postLikeRepository.findByPostAndUser(post, user)).willReturn(Optional.of(postLike));

        AtomicLong likeCount = new AtomicLong(1);
        willAnswer(inv -> { likeCount.decrementAndGet(); return 1; })
                .given(postLikeCountRepository).decrease(1L);
        given(postLikeCountRepository.getCountByPostId(1L))
                .willAnswer(inv -> likeCount.get());

        try (MockedStatic<UserUtil> mocked = mockStatic(UserUtil.class)) {
            mocked.when(UserUtil::getCurrentUser).thenReturn(user);

            // when
            PostLikeDto result = postLikeService.postUnlike(1L);

            // then
            assertThat(result.getLikeCount()).isEqualTo(0L);
            assertThat(result.isLiked()).isFalse();

            verify(postLikeRepository, times(1)).findByPostAndUser(post, user);
            verify(postLikeCountRepository, times(1)).decrease(1L);
            verify(postLikeCountRepository, times(1)).getCountByPostId(1L);
        }
    }

    @Test
    @DisplayName("좋아요 수가 3인 게시글에서 취소 시, 3→2로 감소한다")
    void cancelLikeWhenLikeCountThree_thenLikeCountIsTwo() {
        // given
        Post post = new Post();
        Users user = new Users();
        PostLike postLike = new PostLike();

        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(postLikeRepository.findByPostAndUser(post, user)).willReturn(Optional.of(postLike));

        AtomicLong likeCount = new AtomicLong(3);
        willAnswer(inv -> { likeCount.decrementAndGet(); return 1; })
                .given(postLikeCountRepository).decrease(1L);
        given(postLikeCountRepository.getCountByPostId(1L))
                .willAnswer(inv -> likeCount.get());

        try (MockedStatic<UserUtil> mocked = mockStatic(UserUtil.class)) {
            mocked.when(UserUtil::getCurrentUser).thenReturn(user);

            // when
            PostLikeDto result = postLikeService.postUnlike(1L);

            // then
            assertThat(result.getLikeCount()).isEqualTo(2L);
            assertThat(result.isLiked()).isFalse();
        }
    }

    @Test
    @DisplayName("좋아요 내역이 없는 상태에서 취소 요청 시 예외 발생 및 decrease() 호출 안 됨")
    void cancelLikeWhenNoExistingLike_thenThrowsException() {
        // given
        Post post = new Post();
        Users user = new Users();

        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(postLikeRepository.findByPostAndUser(post, user)).willReturn(Optional.empty());

        try (MockedStatic<UserUtil> mocked = mockStatic(UserUtil.class)) {
            mocked.when(UserUtil::getCurrentUser).thenReturn(user);

            // when
            CustomException ex = assertThrows(CustomException.class,
                    () -> postLikeService.postUnlike(1L));

            // then
            assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.LIKE_NOT_FOUND);
            verify(postLikeCountRepository, never()).decrease(1L);
            verify(postLikeCountRepository, never()).getCountByPostId(1L);
        }
    }
}
