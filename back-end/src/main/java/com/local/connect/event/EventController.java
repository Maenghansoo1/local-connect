package com.local.connect.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// 축제 API
@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // GET /api/events              → 전체 목록
    // GET /api/events?region=서울  → 지역 필터
    // GET /api/events?keyword=벚꽃 → 키워드 검색
    @GetMapping
    public ResponseEntity<Page<EventResponse>> getEvents(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "startDate", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(eventService.getEvents(region, keyword, pageable));
    }

    // GET /api/events/1  → 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEvent(id));
    }

    // POST /api/events/sync  → 공공API에서 데이터 가져오기
    @PostMapping("/sync")
    public ResponseEntity<Void> sync() {
        eventService.syncAll();
        return ResponseEntity.ok().build();
    }
}
