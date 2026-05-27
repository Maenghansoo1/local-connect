package com.project.project.service;

import com.project.project.entity.User;
import com.project.project.entity.VisitHistory;
import com.project.project.repository.UserRepository;
import com.project.project.repository.VisitHistoryRepository;
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

    // 방문 기록 저장 (중복 저장 방지)
    public void save(String username, Map<String, String> data) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        String contentId = data.get("contentId");

        // 이미 방문 기록이 있으면 저장하지 않음
        if (visitHistoryRepository.existsByUserIdAndContentId(user.getId(), contentId)) return;

        VisitHistory history = new VisitHistory();
        history.setUser(user);
        history.setContentId(contentId);
        history.setTitle(data.get("title"));
        history.setAddr(data.get("addr"));
        history.setImage(data.get("image"));
        visitHistoryRepository.save(history);
    }

    // 방문 기록 목록
    public List<VisitHistory> getList(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        return visitHistoryRepository.findByUserIdOrderByVisitedAtDesc(user.getId());
    }
}
