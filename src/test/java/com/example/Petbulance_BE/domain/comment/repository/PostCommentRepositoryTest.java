package com.example.Petbulance_BE.domain.comment.repository;

import com.example.Petbulance_BE.domain.board.entity.Board;
import com.example.Petbulance_BE.domain.board.repository.BoardRepository;
import com.example.Petbulance_BE.domain.comment.dto.response.SearchPostCommentResDto;
import com.example.Petbulance_BE.domain.comment.entity.PostComment;
import com.example.Petbulance_BE.domain.post.entity.Post;

import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.post.type.Category;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.global.common.type.Gender;
import com.example.Petbulance_BE.global.common.type.Role;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test") // MySQL 테스트용 프로필 (application-test.yml)
class PostCommentRepositoryTest {

    @Autowired
    private PostCommentRepositoryImpl postCommentRepositoryImpl;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UsersJpaRepository usersRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    private Users user1, user2;
    private Board board;
    private Post post1, post2;

    @BeforeEach
    void setup() {
        user1 = usersRepository.save(Users.builder()
                .nickname("테스터1")
                .birth(LocalDate.of(1995, 1, 1))
                .gender(Gender.FEMALE)
                .role(Role.ROLE_CLIENT)
                .build());

        user2 = usersRepository.save(Users.builder()
                .nickname("홍길동")
                .birth(LocalDate.of(1990, 2, 2))
                .gender(Gender.MALE)
                .role(Role.ROLE_CLIENT)
                .build());

        board = boardRepository.save(Board.builder()
                .nameKr("자유게시판")
                .nameEn("freeboard")
                .build());

        post1 = postRepository.save(Post.builder()
                .title("강아지 사료 추천")
                .content("내용1")
                .category(Category.SUPPLIES)
                .board(board)
                .user(user1)
                .build());

        post2 = postRepository.save(Post.builder()
                .title("고양이 영양제 추천")
                .content("내용2")
                .category(Category.HEALTH)
                .board(board)
                .user(user2)
                .build());

        // 댓글 데이터
        postCommentRepository.save(PostComment.builder()
                .post(post1)
                .user(user1)
                .content("로얄캐닌 정말 좋아요!")
                .isSecret(false)
                .hidden(false)
                .deleted(false)
                .build());

        postCommentRepository.save(PostComment.builder()
                .post(post1)
                .user(user2)
                .content("이거 진짜 괜찮네요.")
                .isSecret(false)
                .hidden(false)
                .deleted(false)
                .build());

        postCommentRepository.save(PostComment.builder()
                .post(post2)
                .user(user1)
                .content("우리 고양이도 이거 먹어요")
                .isSecret(false)
                .hidden(false)
                .deleted(false)
                .build());
    }

    @Test
    @DisplayName("댓글 내용(content)으로 검색")
    void testFindSearchPostComment_ByContent() {
        // when
        Slice<SearchPostCommentResDto> result = postCommentRepositoryImpl.findSearchPostComment(
                "로얄캐닌",       // keyword
                "content",       // scope
                null,            // lastCommentId
                10,              // pageSize
                null,            // category
                board.getId()    // boardId
        );

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCommentContent()).contains("로얄캐닌");
    }

    @Test
    @DisplayName("작성자 닉네임(writer)으로 검색")
    void testFindSearchPostComment_ByWriter() {
        // when
        Slice<SearchPostCommentResDto> result = postCommentRepositoryImpl.findSearchPostComment(
                "홍길동",
                "writer",
                null,
                10,
                null,
                board.getId()
        );

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getWriterNickname()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("카테고리로 검색")
    void testFindSearchPostComment_ByCategory() {
        // when
        Slice<SearchPostCommentResDto> result = postCommentRepositoryImpl.findSearchPostComment(
                null,
                null,
                null,
                10,
                List.of(Category.SUPPLIES),
                board.getId()
        );

        // then
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getBoardName()).isEqualTo("자유게시판");
    }

    @Test
    @DisplayName("댓글 개수 조회")
    void testCountSearchPostComment() {
        // when
        long count = postCommentRepositoryImpl.countSearchPostComment(
                null,
                null,
                List.of(Category.SUPPLIES, Category.HEALTH),
                board.getId()
        );

        // then
        assertThat(count).isEqualTo(3L);
    }
}
