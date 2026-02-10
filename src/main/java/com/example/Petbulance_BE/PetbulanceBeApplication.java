package com.example.Petbulance_BE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.Petbulance_BE.domain")
public class PetbulanceBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetbulanceBeApplication.class, args);
	}

}
