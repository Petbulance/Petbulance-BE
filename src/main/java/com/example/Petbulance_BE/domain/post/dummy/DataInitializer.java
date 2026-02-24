package com.example.Petbulance_BE.domain.post.dummy;


import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.post.type.Topic;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Profile("local")
public class DataInitializer implements CommandLineRunner {

    private final PostRepository postRepository;
    private final UsersJpaRepository userRepository;

    @Override
    public void run(String... args) {

        //if (postRepository.count() > 0) return; // 중복 생성 방지

        List<Users> users = userRepository.findAll();
        if (users.isEmpty()) return;

        Random random = new Random();

        Topic[] topics = Topic.values();
        AnimalType[] animalTypes = AnimalType.values();

        for (int i = 1; i <= 20; i++) {

            Users randomUser = users.get(random.nextInt(users.size()));
            Topic randomTopic = topics[random.nextInt(topics.length)];
            AnimalType randomType = animalTypes[random.nextInt(animalTypes.length)];

            Post post = Post.builder()
                    .user(randomUser)
                    .topic(randomTopic)
                    .animalType(randomType)
                    .title("테스트 게시글 제목 " + i)
                    .content("이것은 더미 데이터입니다. 게시글 번호: " + i)
                    .imageNum(0)
                    .hidden(false)
                    .deleted(false)
                    .reportCount(0)
                    .build();

            postRepository.save(post);
        }

        System.out.println("✅ 테스트 게시글 20개 생성 완료");
    }
}