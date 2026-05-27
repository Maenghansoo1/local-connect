package com.project.project.service;

import com.project.project.entity.Favorite;
import com.project.project.entity.User;
import com.project.project.repository.FavoriteRepository;
import com.project.project.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
    }

    // 즐겨찾기 추가/취소 토글
    @Transactional
    public Map<String, Object> toggle(String username, Map<String, String> spotData) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        String contentId = spotData.get("contentId");

        if (favoriteRepository.existsByUserIdAndContentId(user.getId(), contentId)) {
            favoriteRepository.deleteByUserIdAndContentId(user.getId(), contentId);
            return Map.of("saved", false, "message", "즐겨찾기가 해제되었습니다.");
        } else {
            Favorite fav = new Favorite();
            fav.setUser(user);
            fav.setContentId(contentId);
            fav.setTitle(spotData.get("title"));
            fav.setAddr(spotData.get("addr"));
            fav.setImage(spotData.get("image"));
            fav.setContentTypeId(spotData.get("contentTypeId"));
            fav.setMapx(spotData.get("mapx"));
            fav.setMapy(spotData.get("mapy"));
            favoriteRepository.save(fav);
            return Map.of("saved", true, "message", "즐겨찾기에 추가되었습니다.");
        }
    }

    // 즐겨찾기 목록 조회
    public List<Favorite> getList(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    // 즐겨찾기 여부 확인
    public boolean isFavorite(String username, String contentId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        return favoriteRepository.existsByUserIdAndContentId(user.getId(), contentId);
    }
}
