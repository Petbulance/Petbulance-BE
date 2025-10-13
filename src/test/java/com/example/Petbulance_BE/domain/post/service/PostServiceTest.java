package com.example.Petbulance_BE.domain.post.service;

import com.example.Petbulance_BE.domain.board.entity.Board;
import com.example.Petbulance_BE.domain.board.repository.BoardRepository;
import com.example.Petbulance_BE.domain.comment.entity.PostComment;
import com.example.Petbulance_BE.domain.comment.repository.PostCommentRepository;
import com.example.Petbulance_BE.domain.post.dto.request.CreatePostCommentReqDto;
import com.example.Petbulance_BE.domain.post.dto.request.CreatePostReqDto;
import com.example.Petbulance_BE.domain.post.dto.response.PostCommentResDto;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.entity.PostImage;
import com.example.Petbulance_BE.domain.post.repository.PostImageRepository;
import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.post.type.Category;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostService테스트")
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private PostImageRepository postImageRepository;

    @Mock
    private PostCommentRepository postCommentRepository;

    @Mock
    private UsersJpaRepository usersJpaRepository;

    @Test
    @DisplayName("게시글 생성 시 이미지 1개 첨부일 때 정상 저장")
    void createPostWithOneImage() {
        // given
        List<String> imageUrls = List.of("https://cdn.example.com/image1.jpg");

        CreatePostReqDto dto = new CreatePostReqDto(
                1L, "HEALTH", "제목", "내용", imageUrls
        );

        Board mockBoard = mock(Board.class);
        given(boardRepository.findById(1L)).willReturn(Optional.of(mockBoard));

        Post post = Post.builder()
                .board(mockBoard)
                .category(Category.HEALTH)
                .title("제목")
                .content("내용")
                .imageNum(1)
                .build();


        given(postRepository.save(any(Post.class))).willReturn(post);

        // when
        Post result = postService.createPost(dto);

        // then
        verify(postImageRepository, times(1)).save(any(PostImage.class)); // 1번 호출 검증
        assertThat(result.getImageNum()).isEqualTo(1);
    }


    @Test
    @DisplayName("게시글 생성 시 이미지 0개 첨부일 때 정상 저장")
    void createPostWithZeroImage() {
        // given
        List<String> imageUrls = List.of();

        CreatePostReqDto dto = new CreatePostReqDto(
                1L, "HEALTH", "제목", "내용", imageUrls
        );

        Board mockBoard = mock(Board.class);
        given(boardRepository.findById(1L)).willReturn(Optional.of(mockBoard));

        Post post = Post.builder()
                .board(mockBoard)
                .category(Category.HEALTH)
                .title("제목")
                .content("내용")
                .imageNum(0)
                .build();

        given(postRepository.save(any(Post.class))).willReturn(post);

        // when
        Post result = postService.createPost(dto);

        // then
        verify(postImageRepository, times(0)).save(any(PostImage.class)); // ✅ 0번 호출되어야 정상
        assertThat(result.getImageNum()).isEqualTo(0);
    }


    @Test
    @DisplayName("제목 또는 본문이 비었을 때 예외 발생 및 ErrorCode 검증")
    void createPostWhenTitleOrContentBlank() {
        // given
        CreatePostReqDto dto = new CreatePostReqDto(
                1L, "HEALTH", "", "본문 내용", List.of()
        );

        // when
        CustomException ex = assertThrows(CustomException.class,
                () -> postService.createPost(dto));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMPTY_TITLE_OR_CONTENT);
    }

    @Test
    @DisplayName("이미지 개수가 10개를 초과할 때 예외 발생 및 ErrorCode 검증")
    void createPostWhenImageCountExceedsLimit() {
        // given
        List<String> imageUrls = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            imageUrls.add("https://cdn.example.com/img" + i + ".jpg");
        }

        CreatePostReqDto dto = new CreatePostReqDto(
                1L, "HEALTH", "제목", "본문 내용", imageUrls
        );

        // when
        CustomException ex = assertThrows(CustomException.class,
                () -> postService.createPost(dto));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EXCEEDED_MAX_IMAGE_COUNT);
    }

    @Test
    @DisplayName("존재하지 않는 게시판 ID 또는 잘못된 카테고리일 때 예외 발생 및 ErrorCode 검증")
    void createPostWhenInvalidBoardOrCategory() {
        // given
        CreatePostReqDto dto = new CreatePostReqDto(
                999L, "INVALID_CATEGORY", "제목", "본문", List.of()
        );

        // 게시판 없음
        given(boardRepository.findById(999L)).willReturn(Optional.empty());

        // when
        CustomException ex = assertThrows(CustomException.class,
                () -> postService.createPost(dto));

        // then
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_BOARD_OR_CATEGORY);
    }

    @Test
    @DisplayName("상위 댓글 작성 성공")
    void createParentCommentSuccess() {
        // given
        Post mockPost = mock(Post.class);
        Users mockUser = mock(Users.class);

        given(postRepository.findById(anyLong())).willReturn(Optional.of(mockPost));

        try (MockedStatic<UserUtil> mockedUserUtil = mockStatic(UserUtil.class)) {
            mockedUserUtil.when(UserUtil::getCurrentUser).thenReturn(mockUser);

            CreatePostCommentReqDto dto = new CreatePostCommentReqDto(
                    "댓글 내용", null, null, "이미지 url", false
            );

            // 저장될 댓글 객체 mock
            PostComment savedComment = PostComment.builder()
                    .post(mockPost)
                    .user(mockUser)
                    .content(dto.getContent())
                    .parent(null)
                    .mentionUser(null)
                    .isSecret(dto.getIsSecret())
                    .imageUrl(dto.getImageUrl())
                    .build();

            given(postCommentRepository.save(any(PostComment.class))).willReturn(savedComment);

            // when
            PostCommentResDto resDto = postService.createPostComment(1L, dto);

            // then
            assertThat(resDto.getContent()).isEqualTo(dto.getContent());
            assertThat(resDto.isSecret()).isEqualTo(dto.getIsSecret());
            assertThat(resDto.getImageUrl()).isEqualTo(dto.getImageUrl());
            assertThat(resDto.getParentId()).isNull();
            assertThat(resDto.getMentionUserNickname()).isNull();

            verify(postCommentRepository, times(1)).save(any(PostComment.class));
        }
    }


    @Test
    @DisplayName("멘션 포함 하위 댓글 작성 성공")
    void createCommentWithMentionSuccess() {
        // given
        Post mockPost = mock(Post.class);
        Users mockWriter = mock(Users.class); // 댓글 작성자
        Users mentionedUser = Users.builder()
                .id(UUID.randomUUID().toString())
                .nickname("mentionedUser")
                .build();

        PostComment parentComment = PostComment.builder()
                .id(100L)
                .post(mockPost)
                .user(mockWriter)
                .content("부모 댓글 내용")
                .build();

        CreatePostCommentReqDto dto = new CreatePostCommentReqDto(
                "멘션 포함 대댓글입니다.",
                parentComment.getId(),
                mentionedUser.getNickname(),
                "image-url.jpg",
                false
        );

        given(postRepository.findById(anyLong())).willReturn(Optional.of(mockPost));
        given(postCommentRepository.findById(parentComment.getId())).willReturn(Optional.of(parentComment));
        given(usersJpaRepository.findByNickname(mentionedUser.getNickname())).willReturn(Optional.of(mentionedUser));

        // UserUtil.getCurrentUser() 정적 메서드 모킹
        try (MockedStatic<UserUtil> mockedUserUtil = mockStatic(UserUtil.class)) {
            mockedUserUtil.when(UserUtil::getCurrentUser).thenReturn(mockWriter);

            // 저장되는 댓글 객체
            PostComment savedComment = PostComment.builder()
                    .post(mockPost)
                    .user(mockWriter)
                    .content(dto.getContent())
                    .parent(parentComment)
                    .mentionUser(mentionedUser)
                    .isSecret(dto.getIsSecret())
                    .imageUrl(dto.getImageUrl())
                    .build();

            given(postCommentRepository.save(any(PostComment.class))).willReturn(savedComment);

            // when
            PostCommentResDto resDto = postService.createPostComment(1L, dto);

            // then
            assertThat(resDto.getContent()).isEqualTo(dto.getContent());
            assertThat(resDto.getParentId()).isEqualTo(parentComment.getId());
            assertThat(resDto.getMentionUserNickname()).isEqualTo(mentionedUser.getNickname());
            assertThat(resDto.getImageUrl()).isEqualTo(dto.getImageUrl());
            assertThat(resDto.isSecret()).isEqualTo(dto.getIsSecret());

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
        assertThatThrownBy(() -> postService.createPostComment(999L, dto))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.POST_NOT_FOUND.getMessage());

        verify(postRepository, times(1)).findById(999L);
        verifyNoInteractions(postCommentRepository, usersJpaRepository);
    }

    @Test
    @DisplayName("존재하지 않는 부모 댓글이면 예외 발생")
    void createComment_InvalidParentComment() {
        // given
        Post mockPost = mock(Post.class);
        given(postRepository.findById(anyLong())).willReturn(Optional.of(mockPost));

        CreatePostCommentReqDto dto = new CreatePostCommentReqDto(
                "대댓글입니다.", 999L, null, null, false
        );

        // parentId=999L에 해당하는 댓글이 없음
        given(postCommentRepository.findById(999L)).willReturn(Optional.empty());

        // UserUtil.getCurrentUser()는 호출되기 전에 예외 발생
        // when & then
        assertThatThrownBy(() -> postService.createPostComment(1L, dto))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_PARENT_COMMENT.getMessage());

        verify(postRepository, times(1)).findById(1L);
        verify(postCommentRepository, times(1)).findById(999L);
        verifyNoInteractions(usersJpaRepository);
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
        assertThatThrownBy(() -> postService.createPostComment(1L, dto))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.INVALID_MENTION_USER.getMessage());

        verify(postRepository, times(1)).findById(1L);
        verify(postCommentRepository, times(1)).findById(parentComment.getId());
        verify(usersJpaRepository, times(1)).findByNickname("ghostUser");
    }

}
