package com.example.Petbulance_BE.domain.banner.repository;


import com.example.Petbulance_BE.domain.banner.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<Banner, Long> {
}
