package com.example.Petbulance_BE.global.common.auth.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;

@Component
@Slf4j
public class RandomNicknameGenerator {

    private List<String> adjectives;
    private List<String> animals;
    private final Random random = new Random();

    public RandomNicknameGenerator(){
        loadFiles();
    }

    private void loadFiles(){
        try{
            // 형용사 리스트
            this.adjectives = new BufferedReader(
                    new InputStreamReader(
                            new ClassPathResource("nickname/adjectives.txt").getInputStream(),
                            StandardCharsets.UTF_8
                    )
            ).lines()
                    .filter(s -> !s.isBlank())
                    .toList();

            // 동물 리스트
            this.animals = new BufferedReader(
                    new InputStreamReader(
                            new ClassPathResource("nickname/animals.txt").getInputStream(),
                            StandardCharsets.UTF_8
                    )
            ).lines()
                    .filter(s -> !s.isBlank())
                    .toList();

            log.info("{}",adjectives);
            log.info("{}",animals);
        } catch (Exception e) {
            log.error("{}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String generateNickname(){
        String adj = adjectives.get(random.nextInt(adjectives.size()));
        String animal = animals.get(random.nextInt(animals.size()));
        int num = random.nextInt(100);

        return adj + animal + String.format("%02d", num);
    }
}
