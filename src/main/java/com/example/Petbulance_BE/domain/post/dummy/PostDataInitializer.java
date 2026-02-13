package com.example.Petbulance_BE.domain.post.dummy;

import com.example.Petbulance_BE.domain.board.entity.Board;
import com.example.Petbulance_BE.domain.board.repository.BoardRepository;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.post.type.Category;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@Profile("local") // dev 환경에서만 실행 권장
@RequiredArgsConstructor
public class PostDataInitializer implements CommandLineRunner {

    private final PostRepository postRepository;
    private final UsersJpaRepository userRepository;
    private final BoardRepository boardRepository;

    @Override
    public void run(String... args) {
        if (postRepository.count() > 0) return;

        List<Users> users = userRepository.findAll();
        List<Board> boards = boardRepository.findAll();

        if (users.isEmpty() || boards.isEmpty()) return;

        Random random = new Random();
        Category[] categories = Category.values();

        for (int i = 1; i <= 30; i++) {
            Users user = users.get(random.nextInt(users.size()));
            Board board = boards.get(random.nextInt(boards.size()));
            Category category = categories[random.nextInt(categories.length)];

            Post post = Post.builder()
                    .user(user)
                    .board(board)
                    .category(category)
                    .title("테스트 게시글 제목 " + i)
                    .content("테스트 게시글 내용입니다. 번호 = " + i)
                    .imageNum(random.nextInt(4)) // 0~3
                    .hidden(false)
                    .deleted(false)
                    .build();

            postRepository.save(post);
        }
    }
}

