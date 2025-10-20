/*
package com.example.Petbulance_BE.domain.comment.service;

import com.example.Petbulance_BE.domain.comment.dto.response.PostCommentListResDto;
import com.example.Petbulance_BE.domain.comment.entity.PostComment;
import com.example.Petbulance_BE.domain.comment.repository.PostCommentRepository;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.global.util.CustomUserDetails;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PostCommentListServiceTest {

    @InjectMocks
    private PostCommentService postCommentService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostCommentRepository postCommentRepository;

    @Mock
    private UsersJpaRepository usersJpaRepository;

    @Test
    @DisplayName("게시글 작성자가 게시글 댓글 조회시")
    void postCommentListWithPostWriter() {
        // given
        Post post = mock(Post.class);

        // 동일한 postWriter mock 객체 생성
        Users postWriter = mock(Users.class);
        String uuid = UUID.randomUUID().toString();
        given(postWriter.getId()).willReturn(uuid);
        given(postWriter.getNickname()).willReturn("게시글 작성자");
        given(postWriter.getProfileImage()).willReturn("프로필1");
        given(post.getUser()).willReturn(postWriter); // 게시글 작성자 연결

        // 로그인 유저 mock
        CustomUserDetails customUserDetails = mock(CustomUserDetails.class);
        given(customUserDetails.getUser().getId()).willReturn(postWriter.getId()); // 동일 객체 사용

        Authentication auth = new UsernamePasswordAuthenticationToken(customUserDetails, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // UUID 기반 repository mock (id 타입 String이므로 anyString)
        given(usersJpaRepository.findById(anyString())).willReturn(Optional.of(postWriter));

        // 댓글 작성자 mock
        Users postViewer1 = mock(Users.class);
        given(postViewer1.getId()).willReturn("2");
        given(postViewer1.getNickname()).willReturn("게시글 조회자1");
        given(postViewer1.getProfileImage()).willReturn("프로필2");

        Users postViewer2 = mock(Users.class);
        given(postViewer2.getId()).willReturn("3");
        given(postViewer2.getNickname()).willReturn("게시글 조회자2");
        given(postViewer2.getProfileImage()).willReturn("프로필3");

        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        // 댓글 mock 생성
        PostComment postComment1 = createPostComment(1L, post, postViewer1, "비밀댓글입니다.", null, null, true, false, false, "이미지 url");
        PostComment postComment2 = createPostComment(2L, post, postViewer2, "하위 비밀댓글입니다.", postComment1, postViewer1, true, false, false, "이미지 url!");
        PostComment postComment3 = createPostComment(3L, post, postViewer1, "숨김 댓글입니다.", null, null, false, false, true, "이미지 url");
        PostComment postComment4 = createPostComment(4L, post, postViewer2, "일반 댓글입니다.", null, null, false, false, false, "이미지 url~~");

        // DTO를 미리 만들어 mock 내부 접근 충돌 방지
        PostCommentListResDto dto1 = new PostCommentListResDto(postComment1);
        PostCommentListResDto dto2 = new PostCommentListResDto(postComment2);
        PostCommentListResDto dto3 = new PostCommentListResDto(postComment3);
        PostCommentListResDto dto4 = new PostCommentListResDto(postComment4);

        // findPostCommentByPost mocking
        given(postCommentRepository.findPostCommentByPost(
                post,
                null,
                null,
                Pageable.ofSize(10),
                true, // 게시글 작성자가 댓글 조회 중
                postWriter
        )).willReturn(new SliceImpl<>(List.of(dto1, dto2, dto3, dto4)));

        // when
        Slice<PostCommentListResDto> result =
                postCommentService.postCommentList(1L, null, null, Pageable.ofSize(10));

        // then
        assertThat(result.getContent().size()).isEqualTo(4);
        PostCommentListResDto first = result.getContent().get(0);

        assertThat(first.getWriterNickname()).isEqualTo("게시글 조회자1");
        assertThat(first.getWriterProfileUrl()).isEqualTo("프로필2");
        assertThat(first.getContent()).isEqualTo("비밀댓글입니다.");
        assertThat(first.getIsPostAuthor()).isTrue();
    }

    private PostComment createPostComment(Long id, Post post, Users user, String content,
                                          PostComment parent, Users mentionUser,
                                          Boolean isSecret, Boolean deleted, Boolean hidden,
                                          String imageUrl) {
        PostComment pc = mock(PostComment.class);
        given(pc.getId()).willReturn(id);
        given(pc.getPost()).willReturn(post);
        given(pc.getUser()).willReturn(user);
        given(pc.getContent()).willReturn(content);
        given(pc.getParent()).willReturn(parent);
        given(pc.getMentionUser()).willReturn(mentionUser);
        given(pc.getIsSecret()).willReturn(isSecret);
        given(pc.getDeleted()).willReturn(deleted);
        given(pc.getHidden()).willReturn(hidden);
        given(pc.getImageUrl()).willReturn(imageUrl);
        return pc;
    }
}
*/
