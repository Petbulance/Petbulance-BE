package com.example.Petbulance_BE.domain.comment.service;

import com.example.Petbulance_BE.domain.comment.dto.response.PagingPostCommentListResDto;
import com.example.Petbulance_BE.domain.comment.dto.response.PostCommentListResDto;
import com.example.Petbulance_BE.domain.comment.repository.PostCommentRepository;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.global.util.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostCommentListServiceTest {

    @InjectMocks
    private PostCommentService postCommentService;

    @Mock private PostRepository postRepository;
    @Mock private PostCommentRepository postCommentRepository;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("게시글 작성자가 게시글 댓글 조회시 - 비밀/숨김 포함 노출")
    void postCommentListWithPostWriter() {
        // --- given: 게시글/작성자/로그인 사용자 설정 ---
        Post post = mock(Post.class);
        Users postWriter = mock(Users.class);
        String writerId = "user-post-writer-id";

        when(postWriter.getId()).thenReturn(writerId);
        when(post.getUser()).thenReturn(postWriter);

        Users loginUser = mock(Users.class);
        when(loginUser.getId()).thenReturn(writerId);

        CustomUserDetails cud = mock(CustomUserDetails.class);
        when(cud.getUser()).thenReturn(loginUser);

        Authentication auth = new UsernamePasswordAuthenticationToken(cud, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));


        PostCommentListResDto dto1 = mock(PostCommentListResDto.class);
        when(dto1.getWriterNickname()).thenReturn("게시글 조회자1");
        when(dto1.getWriterProfileUrl()).thenReturn("프로필2");
        when(dto1.getContent()).thenReturn("비밀댓글입니다.");
        when(dto1.getIsSecret()).thenReturn(true);
        when(dto1.getIsCommentFromPostAuthor()).thenReturn(false);
        when(dto1.getIsCommentAuthor()).thenReturn(false);
        when(dto1.getVisibleToUser()).thenReturn(true);


        PostCommentListResDto dto2 = mock(PostCommentListResDto.class);
        when(dto2.getWriterNickname()).thenReturn("게시글 조회자2");
        when(dto2.getWriterProfileUrl()).thenReturn("프로필3");
        when(dto2.getContent()).thenReturn("하위 비밀댓글입니다.");
        when(dto2.getIsSecret()).thenReturn(true);
        when(dto2.getIsCommentFromPostAuthor()).thenReturn(false);
        when(dto2.getIsCommentAuthor()).thenReturn(false);
        when(dto2.getVisibleToUser()).thenReturn(true);

        PostCommentListResDto dto3 = mock(PostCommentListResDto.class);
        when(dto3.getWriterNickname()).thenReturn("게시글 조회자1");
        when(dto3.getWriterProfileUrl()).thenReturn("프로필2");
        when(dto3.getContent()).thenReturn("숨김 댓글입니다.");
        when(dto3.getIsSecret()).thenReturn(false);
        when(dto3.getHidden()).thenReturn(true);
        when(dto3.getIsCommentFromPostAuthor()).thenReturn(false);
        when(dto3.getIsCommentAuthor()).thenReturn(false);
        when(dto3.getVisibleToUser()).thenReturn(false);

        PostCommentListResDto dto4 = mock(PostCommentListResDto.class);
        when(dto4.getWriterNickname()).thenReturn("게시글 조회자2");
        when(dto4.getWriterProfileUrl()).thenReturn("프로필3");
        when(dto4.getContent()).thenReturn("일반 댓글입니다.");
        when(dto4.getIsSecret()).thenReturn(false);
        when(dto4.getIsCommentFromPostAuthor()).thenReturn(false);
        when(dto4.getIsCommentAuthor()).thenReturn(false);
        when(dto4.getVisibleToUser()).thenReturn(true);

        Slice<PostCommentListResDto> slice =
                new SliceImpl<>(List.of(dto1, dto2, dto3, dto4));


        given(postCommentRepository.findPostCommentByPost(
                any(Post.class),
                any(),
                any(),
                any(Pageable.class),
                anyBoolean(),
                any(Users.class)
        )).willReturn(slice);

        // --- when ---
        PagingPostCommentListResDto result =
                postCommentService.postCommentList(1L, null, null, Pageable.ofSize(10));

        List<PostCommentListResDto> content = result.getContent(); // 메서드명이 다르면 수정

        // --- then: 내용 검증 ---
        assertThat(content).hasSize(4);

        PostCommentListResDto first = content.get(0);
        assertThat(first.getWriterNickname()).isEqualTo("게시글 조회자1");
        assertThat(first.getWriterProfileUrl()).isEqualTo("프로필2");
        assertThat(first.getContent()).isEqualTo("비밀댓글입니다.");
        assertThat(first.getIsSecret()).isTrue();
        assertThat(first.getIsCommentFromPostAuthor()).isFalse();
        assertThat(first.getIsCommentAuthor()).isFalse();
        assertThat(first.getVisibleToUser()).isTrue();

        PostCommentListResDto second = content.get(1);
        assertThat(second.getWriterNickname()).isEqualTo("게시글 조회자2");
        assertThat(second.getWriterProfileUrl()).isEqualTo("프로필3");
        assertThat(second.getContent()).isEqualTo("하위 비밀댓글입니다.");
        assertThat(second.getIsSecret()).isTrue();
        assertThat(second.getIsCommentFromPostAuthor()).isFalse();
        assertThat(second.getIsCommentAuthor()).isFalse();
        assertThat(second.getVisibleToUser()).isTrue();

        PostCommentListResDto third = content.get(2);
        assertThat(third.getWriterNickname()).isEqualTo("게시글 조회자1");
        assertThat(third.getWriterProfileUrl()).isEqualTo("프로필2");
        assertThat(third.getContent()).isEqualTo("숨김 댓글입니다.");
        assertThat(third.getIsSecret()).isFalse();
        assertThat(third.getHidden()).isTrue();
        assertThat(third.getIsCommentFromPostAuthor()).isFalse();
        assertThat(third.getIsCommentAuthor()).isFalse();
        assertThat(third.getVisibleToUser()).isFalse();

        PostCommentListResDto fourth = content.get(3);
        assertThat(fourth.getWriterNickname()).isEqualTo("게시글 조회자2");
        assertThat(fourth.getWriterProfileUrl()).isEqualTo("프로필3");
        assertThat(fourth.getContent()).isEqualTo("일반 댓글입니다.");
        assertThat(fourth.getIsSecret()).isFalse();
        assertThat(fourth.getIsCommentFromPostAuthor()).isFalse();
        assertThat(fourth.getIsCommentAuthor()).isFalse();
        assertThat(fourth.getVisibleToUser()).isTrue();



        // 숨김 댓글도 보이는지(작성자 조회이므로)
        assertThat(content)
                .extracting(PostCommentListResDto::getContent)
                .contains("숨김 댓글입니다.");

        // --- verify: 정확 인자 검증 ---
        verify(postCommentRepository).findPostCommentByPost(
                eq(post),
                isNull(Long.class),
                isNull(Long.class),
                any(Pageable.class),
                eq(true),
                // currentUser는 동일 mock 인스턴스가 아닐 수 있으므로 id로 매칭
                argThat(u -> u != null && writerId.equals(u.getId()))
        );

        // 불필요한 상호작용 없는지(선택)
        verifyNoMoreInteractions(postCommentRepository, postRepository);
    }

    @Test
    @DisplayName("일반 조회자가 게시글 댓글 조회시 - 비밀/숨김 가시성 규칙 검증 (비가시 댓글 content=null)")
    void postCommentListWithNonAuthorViewer_visibilityRules() {
        // --- given: 게시글/작성자/로그인 사용자 설정 ---
        Post post = mock(Post.class);
        Users postAuthor = mock(Users.class);
        when(post.getUser()).thenReturn(postAuthor);
        when(postAuthor.getId()).thenReturn("post-author-id");

        // 로그인 유저 = 조회자1 (작성자 아님)
        Users viewer1 = mock(Users.class);
        when(viewer1.getId()).thenReturn("viewer-1-id");

        CustomUserDetails cud = mock(CustomUserDetails.class);
        when(cud.getUser()).thenReturn(viewer1);
        Authentication auth = new UsernamePasswordAuthenticationToken(cud, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

    /*
     댓글 시나리오 (로그인 유저 = 조회자1):
       1) 일반(O)                -> content 문자열, visible=true
       2) 비밀(O: 멘션자 가정)   -> content 문자열, isSecret=true, visible=true
       3) 비밀(X)                -> content=null,   isSecret=true, visible=false
       4) 숨김(X)                -> content=null,   hidden=true,   visible=false
       5) 일반(O)                -> content 문자열, visible=true
     */

        // 1) 일반(O) -> content, visible만 스텁
        PostCommentListResDto d1 = mock(PostCommentListResDto.class);
        when(d1.getContent()).thenReturn("일반 댓글(조회자1)");
        when(d1.getVisibleToUser()).thenReturn(true);

        // 2) 비밀(O) -> content, isSecret, visible
        PostCommentListResDto d2 = mock(PostCommentListResDto.class);
        when(d2.getContent()).thenReturn("하위 비밀 댓글(조회자2)");
        when(d2.getIsSecret()).thenReturn(true);
        when(d2.getVisibleToUser()).thenReturn(true);

        // 3) 비밀(X) -> isSecret, visible
        PostCommentListResDto d3 = mock(PostCommentListResDto.class);
        when(d3.getIsSecret()).thenReturn(true);
        when(d3.getVisibleToUser()).thenReturn(false);

        // 4) 숨김(X) -> hidden, visible
        PostCommentListResDto d4 = mock(PostCommentListResDto.class);
        when(d4.getHidden()).thenReturn(true);
        when(d4.getVisibleToUser()).thenReturn(false);

        // 5) 일반(O) -> content, visible
        PostCommentListResDto d5 = mock(PostCommentListResDto.class);
        when(d5.getContent()).thenReturn("일반 댓글(조회자3)");
        when(d5.getVisibleToUser()).thenReturn(true);

        Slice<PostCommentListResDto> slice = new SliceImpl<>(List.of(d1, d2, d3, d4, d5));

        given(postCommentRepository.findPostCommentByPost(
                any(Post.class),
                any(), any(),
                any(Pageable.class),
                anyBoolean(),
                any(Users.class)
        )).willReturn(slice);

        // --- when ---
        PagingPostCommentListResDto result =
                postCommentService.postCommentList(1L, null, null, Pageable.ofSize(10));

        List<PostCommentListResDto> content = result.getContent();

        // --- then ---
        assertThat(content).hasSize(5);

        // 1) 일반(O)
        assertThat(content.get(0).getContent()).isEqualTo("일반 댓글(조회자1)");
        assertThat(content.get(0).getVisibleToUser()).isTrue();

        // 2) 비밀(O)
        assertThat(content.get(1).getContent()).isEqualTo("하위 비밀 댓글(조회자2)");
        assertThat(content.get(1).getIsSecret()).isTrue();
        assertThat(content.get(1).getVisibleToUser()).isTrue();

        // 3) 비밀(X) → content=null
        assertThat(content.get(2).getContent()).isNull();
        assertThat(content.get(2).getIsSecret()).isTrue();
        assertThat(content.get(2).getVisibleToUser()).isFalse();

        // 4) 숨김(X) → content=null
        assertThat(content.get(3).getContent()).isNull();
        assertThat(content.get(3).getHidden()).isTrue();
        assertThat(content.get(3).getVisibleToUser()).isFalse();

        // 5) 일반(O)
        assertThat(content.get(4).getContent()).isEqualTo("일반 댓글(조회자3)");
        assertThat(content.get(4).getVisibleToUser()).isTrue();

        verify(postCommentRepository).findPostCommentByPost(
                eq(post),
                isNull(Long.class),
                isNull(Long.class),
                any(Pageable.class),
                eq(false), // 작성자 아님
                argThat(u -> u != null && "viewer-1-id".equals(u.getId()))
        );
        verifyNoMoreInteractions(postCommentRepository, postRepository);
    }

}
