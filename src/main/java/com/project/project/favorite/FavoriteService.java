package com.project.project.favorite;

import com.project.project.auth.User;
import com.project.project.auth.UserRepository;
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

    public List<Favorite> getList(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public boolean isFavorite(String username, String contentId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        return favoriteRepository.existsByUserIdAndContentId(user.getId(), contentId);
    }
}
