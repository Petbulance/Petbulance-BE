package com.example.Petbulance_BE.domain.category.repository;

import com.example.Petbulance_BE.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryJpaRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c.name FROM Category c")
    List<String> findAllName();

}
