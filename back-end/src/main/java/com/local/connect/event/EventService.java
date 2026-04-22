package com.local.connect.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.NoSuchElementException;

// 축제 비즈니스 로직
@Service
@Transactional
public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    // 지역코드 → 지역명 매핑
    private static final Map<String, String> AREA_CODES = Map.ofEntries(
            Map.entry("1", "서울"), Map.entry("2", "인천"), Map.entry("3", "대전"),
            Map.entry("4", "대구"), Map.entry("5", "광주"), Map.entry("6", "부산"),
            Map.entry("7", "울산"), Map.entry("8", "세종"), Map.entry("31", "경기"),
            Map.entry("32", "강원"), Map.entry("33", "충북"), Map.entry("34", "충남"),
            Map.entry("35", "경북"), Map.entry("36", "경남"), Map.entry("37", "전북"),
            Map.entry("38", "전남"), Map.entry("39", "제주")
    );

    private final TourApiClient tourApiClient;
    private final EventRepository eventRepository;

    public EventService(TourApiClient tourApiClient, EventRepository eventRepository) {
        this.tourApiClient = tourApiClient;
        this.eventRepository = eventRepository;
    }

    // 공공API에서 전국 축제 데이터를 가져와 DB에 저장
    public void syncAll() {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        AREA_CODES.forEach((code, region) -> {
            try {
                var items = tourApiClient.fetchFestivals(code, today);
                items.forEach(item -> upsert(item, region));
                log.info("동기화 완료 - {}: {}건", region, items.size());
            } catch (Exception e) {
                log.error("동기화 실패 - {}", region, e);
            }
        });
    }

    // 있으면 수정, 없으면 새로 저장
    private void upsert(EventItem item, String region) {
        Double lat = parseDouble(item.getMapy());
        Double lng = parseDouble(item.getMapx());
        eventRepository.findByContentId(item.getContentid())
                .ifPresentOrElse(
                        e -> e.update(item.getTitle(), item.getEventstartdate(), item.getEventenddate(),
                                item.getAddr1(), region, item.getFirstimage(), item.getTel(), lat, lng),
                        () -> eventRepository.save(Event.create(item.getContentid(), item.getTitle(),
                                item.getEventstartdate(), item.getEventenddate(),
                                item.getAddr1(), region, item.getFirstimage(), item.getTel(), lat, lng))
                );
    }

    // 목록 조회 (지역 필터, 키워드 검색 지원)
    @Transactional(readOnly = true)
    public Page<EventResponse> getEvents(String region, String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return eventRepository.findByTitleContaining(keyword, pageable).map(EventResponse::from);
        }
        if (region != null && !region.isBlank()) {
            return eventRepository.findByRegionContaining(region, pageable).map(EventResponse::from);
        }
        return eventRepository.findAll(pageable).map(EventResponse::from);
    }

    // 단건 조회
    @Transactional(readOnly = true)
    public EventResponse getEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("축제를 찾을 수 없습니다. id=" + id));
        return EventResponse.from(event);
    }

    private Double parseDouble(String value) {
        if (value == null || value.isBlank()) return null;
        try { return Double.parseDouble(value); }
        catch (NumberFormatException e) { return null; }
    }
}
