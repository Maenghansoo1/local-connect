package com.project.project.favorite;

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

    @PostMapping("/toggle")
    public ResponseEntity<?> toggle(@RequestBody Map<String, String> spotData,
                                    Authentication authentication) {
        return ResponseEntity.ok(favoriteService.toggle(authentication.getName(), spotData));
    }

    @GetMapping
    public ResponseEntity<?> getList(Authentication authentication) {
        return ResponseEntity.ok(favoriteService.getList(authentication.getName()));
    }

    // permitAll — 비로그인 사용자는 saved:false 반환 (Security 통과 후 컨트롤러에서 처리)
    @GetMapping("/check")
    public ResponseEntity<?> check(@RequestParam String contentId,
                                   Authentication authentication) {
        if (authentication == null) return ResponseEntity.ok(Map.of("saved", false));
        return ResponseEntity.ok(Map.of("saved",
                favoriteService.isFavorite(authentication.getName(), contentId)));
    }
}
