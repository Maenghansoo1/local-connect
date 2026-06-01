package com.project.project.review;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Map<String, String> data,
                                  Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        reviewService.save(authentication.getName(), data);
        return ResponseEntity.ok(Map.of("message", "리뷰가 등록되었습니다."));
    }

    @GetMapping("/spot/{contentId}")
    public ResponseEntity<?> getBySpot(@PathVariable String contentId) {
        return ResponseEntity.ok(reviewService.getBySpot(contentId));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyReviews(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        return ResponseEntity.ok(reviewService.getMyReviews(authentication.getName()));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> delete(@PathVariable Long reviewId, Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        reviewService.delete(authentication.getName(), reviewId);
        return ResponseEntity.ok(Map.of("message", "리뷰가 삭제되었습니다."));
    }
}
