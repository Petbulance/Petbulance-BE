package com.example.Petbulance_BE.domain.post.service;

import com.example.Petbulance_BE.domain.board.entity.Board;
import com.example.Petbulance_BE.domain.board.repository.BoardRepository;
import com.example.Petbulance_BE.domain.comment.repository.PostCommentRepository;
import com.example.Petbulance_BE.domain.post.dto.request.CreatePostReqDto;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.entity.PostImage;
import com.example.Petbulance_BE.domain.post.repository.PostImageRepository;
import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.post.type.Category;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.global.common.error.exception.CustomException;
import com.example.Petbulance_BE.global.common.error.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

}
