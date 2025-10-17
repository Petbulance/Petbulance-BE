package com.example.Petbulance_BE.domain.comment.service;

import com.example.Petbulance_BE.domain.comment.dto.request.UpdatePostCommentReqDto;
import com.example.Petbulance_BE.domain.comment.entity.PostComment;
import com.example.Petbulance_BE.domain.comment.repository.PostCommentCountRepository;
import com.example.Petbulance_BE.domain.comment.repository.PostCommentRepository;
import com.example.Petbulance_BE.domain.post.dto.request.CreatePostCommentReqDto;
import com.example.Petbulance_BE.domain.comment.dto.response.PostCommentResDto;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import com.example.Petbulance_BE.global.util.UserUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostCommentServiceTest {

    @InjectMocks
    private PostCommentService postCommentService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostCommentRepository postCommentRepository;

    @Mock
    private UsersJpaRepository usersJpaRepository;

    @Mock
    private PostCommentCountRepository postCommentCountRepository;

    @Test
    @DisplayName("상위 댓글 작성 후 댓글 수 증가 확인")
    void createParentCommentAndCheckCount() {
        // given
        Post mockPost = mock(Post.class);  // 게시글 mock
        Users mockUser = mock(Users.class);  // 댓글 작성자 mock

        // 게시글 조회 mock
        given(postRepository.findById(anyLong())).willReturn(Optional.of(mockPost));

        // UserUtil.getCurrentUser() mock
        try (MockedStatic<UserUtil> mockedUserUtil = mockStatic(UserUtil.class)) {
            mockedUserUtil.when(UserUtil::getCurrentUser).thenReturn(mockUser);

            // 상위 댓글 DTO 생성
            CreatePostCommentReqDto dto = new CreatePostCommentReqDto(
                    "상위 댓글 내용", null, null, "이미지 url", false
            );

            // 상위 댓글 저장되는 객체 mock
            PostComment savedComment = PostComment.builder()
                    .post(mockPost)
                    .user(mockUser)
                    .content(dto.getContent())
                    .parent(null)
                    .mentionUser(null)
                    .isSecret(dto.getIsSecret())
                    .imageUrl(dto.getImageUrl())
                    .build();

            // 댓글 수 증가 mock (1로 증가)
            given(postCommentCountRepository.increase(anyLong())).willReturn(1);

            // 댓글 저장 mock
            given(postCommentRepository.save(any(PostComment.class))).willReturn(savedComment);

            // when
            PostCommentResDto resDto = postCommentService.createPostComment(1L, dto);

            // then
            assertThat(resDto.getContent()).isEqualTo(dto.getContent());
            assertThat(resDto.isSecret()).isEqualTo(dto.getIsSecret());
            assertThat(resDto.getImageUrl()).isEqualTo(dto.getImageUrl());
            assertThat(resDto.getParentId()).isNull();
            assertThat(resDto.getMentionUserNickname()).isNull();

            // 댓글 수 증가 확인 (1회 호출)
            verify(postCommentCountRepository, times(1)).increase(anyLong());
            verify(postCommentRepository, times(1)).save(any(PostComment.class));
        }
    }

    @Test
    @DisplayName("하위 댓글 작성 후 댓글 수 증가 확인")
    void createChildCommentAndCheckCount() {
        // given
        Post mockPost = mock(Post.class);  // 게시글 mock
        Users mockWriter = mock(Users.class);  // 댓글 작성자 mock
        Users mentionedUser = Users.builder()
                .id(UUID.randomUUID().toString())
                .nickname("mentionedUser")
                .build();  // 멘션된 유저 mock

        // 상위 댓글 mock
        PostComment parentComment = PostComment.builder()
                .id(100L)
                .post(mockPost)
                .user(mockWriter)
                .content("상위 댓글 내용")
                .build();

        // 하위 댓글 DTO 생성
        CreatePostCommentReqDto dto = new CreatePostCommentReqDto(
                "하위 댓글 내용", parentComment.getId(), mentionedUser.getNickname(), "image-url.jpg", false
        );

        // 게시글, 상위 댓글 조회 mock
        given(postRepository.findById(anyLong())).willReturn(Optional.of(mockPost));
        given(postCommentRepository.findById(parentComment.getId())).willReturn(Optional.of(parentComment));
        given(usersJpaRepository.findByNickname(mentionedUser.getNickname())).willReturn(Optional.of(mentionedUser));

        // UserUtil.getCurrentUser() mock
        try (MockedStatic<UserUtil> mockedUserUtil = mockStatic(UserUtil.class)) {
            mockedUserUtil.when(UserUtil::getCurrentUser).thenReturn(mockWriter);

            // 하위 댓글 저장되는 객체 mock
            PostComment savedComment = PostComment.builder()
                    .post(mockPost)
                    .user(mockWriter)
                    .content(dto.getContent())
                    .parent(parentComment)
                    .mentionUser(mentionedUser)
                    .isSecret(dto.getIsSecret())
                    .imageUrl(dto.getImageUrl())
                    .build();

            // 댓글 수 증가 mock (2로 증가)
            given(postCommentCountRepository.increase(anyLong())).willReturn(2);

            // 댓글 저장 mock
            given(postCommentRepository.save(any(PostComment.class))).willReturn(savedComment);

            // when
            PostCommentResDto resDto = postCommentService.createPostComment(1L, dto);

            // then
            assertThat(resDto.getContent()).isEqualTo(dto.getContent());
            assertThat(resDto.getParentId()).isEqualTo(parentComment.getId());
            assertThat(resDto.getMentionUserNickname()).isEqualTo(mentionedUser.getNickname());
            assertThat(resDto.getImageUrl()).isEqualTo(dto.getImageUrl());
            assertThat(resDto.isSecret()).isEqualTo(dto.getIsSecret());

            // 댓글 수 증가 확인 (1회 호출)
            verify(postCommentCountRepository, times(1)).increase(anyLong());
            verify(postCommentRepository, times(1)).save(any(PostComment.class));
        }
    }



    @Test
    @DisplayName("게시글이 존재하지 않으면 예외 발생")
    void createComment_PostNotFound() {
        // given
        CreatePostCommentReqDto dto = new CreatePostCommentReqDto(
                "댓글 내용", null, null, null, false
        );

        // 게시글 ID 999L은 존재하지 않음
        given(postRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postCommentService.createPostComment(999L, dto))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.POST_NOT_FOUND.getMessage());

        verify(postRepository, times(1)).findById(999L);
        verifyNoInteractions(postCommentRepository, usersJpaRepository);
    }

    @Test
    @DisplayName("존재하지 않는 멘션 사용자면 예외 발생")
    void createComment_InvalidMentionUser() {
        // given
        Post mockPost = mock(Post.class);
        Users mockUser = mock(Users.class);
        PostComment parentComment = PostComment.builder()
                .id(10L)
                .post(mockPost)
                .user(mockUser)
                .content("부모 댓글")
                .build();

        CreatePostCommentReqDto dto = new CreatePostCommentReqDto(
                "멘션 테스트 댓글", parentComment.getId(), "ghostUser", null, false
        );

        given(postRepository.findById(anyLong())).willReturn(Optional.of(mockPost));
        given(postCommentRepository.findById(parentComment.getId())).willReturn(Optional.of(parentComment));
        given(usersJpaRepository.findByNickname("ghostUser")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postCommentService.createPostComment(1L, dto))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_MENTION_USER.getMessage());

        verify(postRepository, times(1)).findById(1L);
        verify(postCommentRepository, times(1)).findById(parentComment.getId());
        verify(usersJpaRepository, times(1)).findByNickname("ghostUser");
    }

    @Test
    @DisplayName("삭제할 댓글이 자식 있으면, 삭제 표시만 한다.")
    void deleteShouldMarkDeletedIfHasChildren() {
        // given
        Long commentId = 1L;
        PostComment postComment = createPostComment(commentId);

        // mock: 댓글 조회
        given(postCommentRepository.findById(commentId)).willReturn(Optional.of(postComment));

        // 자식 댓글이 있는 경우
        given(postCommentRepository.countByParent(postComment)).willReturn(2L);

        // when
        postCommentService.deletePostComment(commentId);

        // then
        verify(postComment).delete(); // 삭제 표시만 되었는지 확인
    }

    private PostComment createPostComment(Long commentId) {
        PostComment postComment = mock(PostComment.class);
     //   given(postComment.getId()).willReturn(commentId);
        return postComment;
    }


    @Test
    @DisplayName("하위 댓글이 삭제되고, 삭제되지 않은 부모면, 하위 댓글만 삭제한다.")
    void deleteShouldDeleteChildOnlyIfNotDeletedParent() {
        // given
        Long commentId = 1L;
        Long parentCommentId = 2L;

        // 하위 댓글 mock
        PostComment postComment = createPostComment(commentId, parentCommentId); // 하위 댓글
        // 상위 댓글 mock
        PostComment parentComment = createPostComment(parentCommentId, null); // 부모 댓글

        // 상위 댓글이 삭제되지 않았고, 자식 댓글이 하나 있는 경우
        given(postCommentRepository.findById(commentId)).willReturn(Optional.of(postComment));
        given(postCommentRepository.countByParent(postComment)).willReturn(0L); // 자식댓글이 없음
        given(postCommentRepository.findById(parentCommentId)).willReturn(Optional.of(parentComment));
        given(parentComment.getDeleted()).willReturn(false); // 부모 댓글은 삭제되지 않음

        // when
        postCommentService.deletePostComment(commentId); // 하위 댓글 삭제 시도

        // then
        verify(postCommentRepository).delete(postComment); // 하위 댓글은 삭제됨
        verify(postCommentRepository, never()).delete(parentComment); // 부모 댓글은 삭제되지 않음
    }


    @Test
    @DisplayName("하위 댓글이 삭제되고, 삭제된 부모면, 재귀적으로 모두 삭제한다.")
    void deleteShouldDeleteAllRecursivelyIfDeletedParent() {
        // given
        Long commentId = 1L;
        Long parentCommentId = 2L;

        // 하위 댓글 mock
        PostComment postComment = createPostComment(commentId, parentCommentId); // 하위 댓글
        given(postComment.isRoot()).willReturn(false); // 하위 댓글은 root가 아님

        // 부모 댓글 mock (삭제된 상태)
        PostComment parentPostComment = createPostComment(parentCommentId, null); // 부모 댓글
        given(parentPostComment.isRoot()).willReturn(true); // 부모 댓글은 root
        given(parentPostComment.getDeleted()).willReturn(true); // 부모 댓글은 이미 삭제됨

        // 상위 댓글이 삭제된 상태이고, 자식 댓글이 하나 있는 경우
        given(postCommentRepository.findById(commentId)).willReturn(Optional.of(postComment));
        given(postCommentRepository.countByParent(postComment)).willReturn(0L); // 자식 댓글이 없음
        given(postCommentRepository.findById(parentCommentId)).willReturn(Optional.of(parentPostComment));
        given(postCommentRepository.countByParent(parentPostComment)).willReturn(0L); // 부모 댓글에 자식 댓글 없음

        // when
        postCommentService.deletePostComment(commentId); // 하위 댓글 삭제 시도

        // then
        verify(postCommentRepository).delete(postComment); // 하위 댓글은 삭제됨
        verify(postCommentRepository).delete(parentPostComment); // 부모 댓글도 삭제됨
    }

    private PostComment createPostComment(Long commentId, Long parentCommentId) {
        PostComment postComment = mock(PostComment.class);

        // 부모 댓글 설정
        if (parentCommentId != null) {
            PostComment parentComment = mock(PostComment.class);
            given(parentComment.getId()).willReturn(parentCommentId);
            given(postComment.getParent()).willReturn(parentComment); // 부모 댓글 설정
        }
        return postComment;
    }



}