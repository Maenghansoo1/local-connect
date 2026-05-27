package com.project.project.repository;

import com.project.project.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByContentIdOrderByCreatedAtDesc(String contentId);
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);
}
