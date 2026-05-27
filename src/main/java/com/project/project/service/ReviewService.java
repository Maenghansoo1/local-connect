package com.project.project.service;

import com.project.project.entity.Review;
import com.project.project.entity.User;
import com.project.project.repository.ReviewRepository;
import com.project.project.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    // 리뷰 저장
    public void save(String username, Map<String, String> data) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        Review review = new Review();
        review.setUser(user);
        review.setContentId(data.get("contentId"));
        review.setSpotTitle(data.get("spotTitle"));
        review.setContent(data.get("content"));
        review.setRating(Integer.parseInt(data.get("rating")));
        reviewRepository.save(review);
    }

    // 특정 관광지 리뷰 목록
    public List<Review> getBySpot(String contentId) {
        return reviewRepository.findByContentIdOrderByCreatedAtDesc(contentId);
    }

    // 내 리뷰 목록
    public List<Review> getMyReviews(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    // 리뷰 삭제
    public void delete(String username, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰 없음"));
        if (!review.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("삭제 권한 없음");
        }
        reviewRepository.delete(review);
    }
}
