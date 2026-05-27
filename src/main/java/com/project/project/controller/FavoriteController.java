package com.project.project.controller;

import com.project.project.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    // 즐겨찾기 추가/취소
    @PostMapping("/toggle")
    public ResponseEntity<?> toggle(@RequestBody Map<String, String> spotData,
                                    Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        return ResponseEntity.ok(favoriteService.toggle(authentication.getName(), spotData));
    }

    // 즐겨찾기 목록
    @GetMapping
    public ResponseEntity<?> getList(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        return ResponseEntity.ok(favoriteService.getList(authentication.getName()));
    }

    // 즐겨찾기 여부 확인
    @GetMapping("/check")
    public ResponseEntity<?> check(@RequestParam String contentId, Authentication authentication) {
        if (authentication == null) return ResponseEntity.ok(Map.of("saved", false));
        return ResponseEntity.ok(Map.of("saved", favoriteService.isFavorite(authentication.getName(), contentId)));
    }
}
