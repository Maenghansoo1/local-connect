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
    // GET /api/events?lang=en      → 영문 목록
    @GetMapping
    public ResponseEntity<Page<EventResponse>> getEvents(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "ko") String lang,
            @PageableDefault(size = 20, sort = "startDate", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(eventService.getEvents(region, keyword, lang, pageable));
    }

    // GET /api/events/1        → 상세 조회 (한국어)
    // GET /api/events/1?lang=en → 상세 조회 (영어)
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEvent(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "ko") String lang
    ) {
        return ResponseEntity.ok(eventService.getEvent(id, lang));
    }

    // POST /api/events/sync     → 한국어 데이터 동기화
    @PostMapping("/sync")
    public ResponseEntity<Void> sync() {
        eventService.syncAll();
        return ResponseEntity.ok().build();
    }

    // POST /api/events/sync/en  → 영문 번역 데이터 동기화
    @PostMapping("/sync/en")
    public ResponseEntity<Void> syncEnglish() {
        eventService.syncEnglish();
        return ResponseEntity.ok().build();
    }
}
