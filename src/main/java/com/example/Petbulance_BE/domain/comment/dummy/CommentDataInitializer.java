package com.example.Petbulance_BE.domain.comment.dummy;

import com.example.Petbulance_BE.domain.comment.entity.PostComment;
import com.example.Petbulance_BE.domain.comment.repository.PostCommentRepository;
import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Profile("local")
public class CommentDataInitializer implements ApplicationRunner {

    private final PostRepository postRepository;
    private final UsersJpaRepository usersJpaRepository;
    private final PostCommentRepository postCommentRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // 이미 댓글이 충분히 있으면 스킵하고 싶으면 아래 조건 사용
        // if (postCommentRepository.count() > 0) return;

        List<Post> posts = postRepository.findAll(
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"))
        ).getContent();

        if (posts.isEmpty()) return;

        List<Users> users = usersJpaRepository.findAll();
        if (users.isEmpty()) return;

        for (Post post : posts) {
            for (int i = 1; i <= 5; i++) {
                Users writer = users.get(ThreadLocalRandom.current().nextInt(users.size()));

                // 1) 루트 댓글 먼저 생성 (parent 미지정)
                PostComment root = PostComment.builder()
                        .post(post)
                        .user(writer)
                        .content("더미 댓글 " + i + " (postId=" + post.getId() + ")")
                        .isSecret(false)
                        .deleted(false)
                        .hidden(false)
                        .imageUrl(null)
                        .isCommentFromPostAuthor(writer.getId() != null && writer.getId().equals(post.getUser().getId()))
                        .reportCount(0)
                        .build();

                // 2) 저장해서 id 확보
                root = postCommentRepository.save(root);

                // 3) 루트 댓글 규칙: parent = self 로 세팅 (너의 isRoot() 로직에 맞춤)
                root.assignParent(root);

                postCommentRepository.save(root);
            }
        }
    }
}