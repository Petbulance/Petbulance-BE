package com.example.Petbulance_BE.domain.post.dummy;

import com.example.Petbulance_BE.domain.post.entity.Post;
import com.example.Petbulance_BE.domain.post.repository.PostRepository;
import com.example.Petbulance_BE.domain.post.type.Topic;
import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.global.common.type.AnimalType;
import com.example.Petbulance_BE.global.common.type.Gender;
import com.example.Petbulance_BE.global.common.type.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Profile("local")
public class DataInitializer implements CommandLineRunner {

    private final PostRepository postRepository;
    private final UsersJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {

        if (userRepository.count() == 0) {
            createDummyUsers();
            System.out.println("✅ 테스트 유저 10명 생성 완료");
        }

        if (postRepository.count() == 0) {
            createDummyPosts();
            System.out.println("✅ 테스트 게시글 20개 생성 완료");
        }
    }

    private void createDummyUsers() {

        List<Users> users = new ArrayList<>();

        for (int i = 1; i <= 9; i++) {
            users.add(
                    Users.builder()
                            .nickname("유저" + i)
                            .password(passwordEncoder.encode("1234"))
                            .phoneNumber("0100000000" + i)
                            .phoneNumberConnected(true)
                            .profileImage("https://petbulance-s3-bucket.s3.ap-northeast-2.amazonaws.com/test/profile/default.jpg")
                            .role(Role.ROLE_CLIENT)
                            .gender(i % 2 == 0 ? Gender.FEMALE : Gender.MALE)
                            .firstLogin("KAKAO")
                            .kakaoConnected(true)
                            .birth(LocalDate.of(1995, 1, i))
                            .build()
            );
        }

        // 관리자 계정 추가
        users.add(
                Users.builder()
                        .nickname("관리자")
                        .password(passwordEncoder.encode("admin1234"))
                        .phoneNumber("01099999999")
                        .phoneNumberConnected(true)
                        .profileImage("https://petbulance-s3-bucket.s3.ap-northeast-2.amazonaws.com/test/profile/admin.jpg")
                        .role(Role.ROLE_ADMIN)
                        .gender(Gender.MALE)
                        .firstLogin("GOOGLE")
                        .googleConnected(true)
                        .birth(LocalDate.of(1990, 1, 1))
                        .build()
        );

        userRepository.saveAll(users);
    }

    private void createDummyPosts() {

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
                    .imageNum(random.nextInt(3)) // 0~2장 랜덤
                    .hidden(false)
                    .deleted(false)
                    .reportCount(0)
                    .build();

            postRepository.save(post);
        }
    }
}