package com.project.project.controller;

import com.project.project.service.VisitHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/visits")
public class VisitHistoryController {

    private final VisitHistoryService visitHistoryService;

    public VisitHistoryController(VisitHistoryService visitHistoryService) {
        this.visitHistoryService = visitHistoryService;
    }

    // 방문 기록 저장
    @PostMapping
    public ResponseEntity<?> save(@RequestBody Map<String, String> data,
                                  Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        visitHistoryService.save(authentication.getName(), data);
        return ResponseEntity.ok(Map.of("message", "방문 기록이 저장되었습니다."));
    }

    // 방문 기록 목록
    @GetMapping
    public ResponseEntity<?> getList(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        return ResponseEntity.ok(visitHistoryService.getList(authentication.getName()));
    }
}
