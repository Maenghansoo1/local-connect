package com.project.project.schedule;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // 내 일정 전체 목록
    @GetMapping
    public ResponseEntity<?> getMySchedules(Authentication authentication) {
        return ResponseEntity.ok(scheduleService.getMySchedules(authentication.getName()));
    }

    // 새 일정 생성
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, String> data,
                                    Authentication authentication) {
        return ResponseEntity.ok(scheduleService.create(authentication.getName(), data));
    }

    // 일정 상세 조회 (장소 포함)
    @GetMapping("/{id}")
    public ResponseEntity<?> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getDetail(id));
    }

    // 일정 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication authentication) {
        scheduleService.delete(authentication.getName(), id);
        return ResponseEntity.ok(Map.of("message", "일정이 삭제되었습니다."));
    }

    // 일정에 장소 추가
    @PostMapping("/{scheduleId}/items")
    public ResponseEntity<?> addItem(@PathVariable Long scheduleId,
                                     @RequestBody Map<String, String> data,
                                     Authentication authentication) {
        return ResponseEntity.ok(scheduleService.addItem(authentication.getName(), scheduleId, data));
    }

    // 일정에서 장소 삭제
    @DeleteMapping("/{scheduleId}/items/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Long scheduleId,
                                        @PathVariable Long itemId,
                                        Authentication authentication) {
        scheduleService.deleteItem(authentication.getName(), scheduleId, itemId);
        return ResponseEntity.ok(Map.of("message", "장소가 삭제되었습니다."));
    }
}
