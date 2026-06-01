package com.project.project.visit;

import com.project.project.auth.User;
import com.project.project.auth.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VisitHistoryService {

    private final VisitHistoryRepository visitHistoryRepository;
    private final UserRepository userRepository;

    public VisitHistoryService(VisitHistoryRepository visitHistoryRepository, UserRepository userRepository) {
        this.visitHistoryRepository = visitHistoryRepository;
        this.userRepository = userRepository;
    }

    public void save(String username, Map<String, String> data) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        String contentId = data.get("contentId");
        if (visitHistoryRepository.existsByUserIdAndContentId(user.getId(), contentId)) return;

        VisitHistory history = new VisitHistory();
        history.setUser(user);
        history.setContentId(contentId);
        history.setTitle(data.get("title"));
        history.setAddr(data.get("addr"));
        history.setImage(data.get("image"));
        visitHistoryRepository.save(history);
    }

    public List<VisitHistory> getList(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        return visitHistoryRepository.findByUserIdOrderByVisitedAtDesc(user.getId());
    }
}
