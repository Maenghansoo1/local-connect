package com.local.connect.schedule;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 일정 저장/삭제/조회 API
@RestController
@RequestMapping("/api/schedules")
public class UserScheduleController {

    private final UserScheduleService scheduleService;

    public UserScheduleController(UserScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // POST /api/schedules  →  일정 저장
    @PostMapping
    public ResponseEntity<ScheduleResponse> saveSchedule(@RequestBody ScheduleRequest request,
                                                          Authentication auth) {
        String email = auth.getName(); // 로그인한 유저 이메일
        return ResponseEntity.ok(scheduleService.saveSchedule(
                email,
                request.getEventId(),
                request.getStartDate(),  // 사용자가 달력에서 선택한 날짜
                request.getEndDate()
        ));
    }

    // DELETE /api/schedules/{id}  →  일정 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id, Authentication auth) {
        String email = auth.getName();
        scheduleService.deleteSchedule(id, email);
        return ResponseEntity.ok().build();
    }

    // GET /api/schedules  →  내 일정 목록
    @GetMapping
    public ResponseEntity<List<ScheduleResponse>> getMySchedules(Authentication auth) {
        String email = auth.getName();
        return ResponseEntity.ok(scheduleService.getMySchedules(email));
    }
}
