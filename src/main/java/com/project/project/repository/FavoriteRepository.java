package com.project.project.repository;

import com.project.project.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Favorite> findByUserIdAndContentId(Long userId, String contentId);
    boolean existsByUserIdAndContentId(Long userId, String contentId);
    void deleteByUserIdAndContentId(Long userId, String contentId);
}
