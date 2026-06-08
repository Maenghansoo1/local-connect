package com.project.project.review;

import com.project.project.auth.User;
import com.project.project.auth.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public void save(String username, Map<String, String> data) {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new IllegalArgumentException("사용자 없음");
        Review review = new Review();
        review.setUserId(user.getId());
        review.setContentId(data.get("contentId"));
        review.setSpotTitle(data.get("spotTitle"));
        review.setContent(data.get("content"));
        review.setRating(Integer.parseInt(data.get("rating")));
        review.setCreatedAt(LocalDateTime.now());
        reviewRepository.insert(review);
    }

    public List<Review> getBySpot(String contentId) {
        return reviewRepository.findByContentIdOrderByCreatedAtDesc(contentId);
    }

    public List<Review> getMyReviews(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new IllegalArgumentException("사용자 없음");
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public void delete(String username, Long reviewId) {
        Review review = reviewRepository.findById(reviewId);
        if (review == null) throw new IllegalArgumentException("리뷰 없음");
        if (!review.getUsername().equals(username)) {
            throw new IllegalArgumentException("삭제 권한 없음");
        }
        reviewRepository.deleteById(reviewId);
    }
}
