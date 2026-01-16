package com.example.Petbulance_BE.domain.banner.repository;


import com.example.Petbulance_BE.domain.banner.entity.Banner;
import com.example.Petbulance_BE.domain.notice.type.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<Banner, Long>, BannerRepositoryCustom {
    long countByPostStatus(PostStatus postStatus);
}
