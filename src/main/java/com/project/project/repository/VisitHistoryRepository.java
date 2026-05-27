package com.project.project.repository;

import com.project.project.entity.VisitHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitHistoryRepository extends JpaRepository<VisitHistory, Long> {
    List<VisitHistory> findByUserIdOrderByVisitedAtDesc(Long userId);
    boolean existsByUserIdAndContentId(Long userId, String contentId);
}
