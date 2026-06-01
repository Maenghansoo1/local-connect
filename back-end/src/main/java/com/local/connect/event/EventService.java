package com.local.connect.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private final EventTranslationRepository translationRepository;

    public EventService(TourApiClient tourApiClient, EventRepository eventRepository,
                        EventTranslationRepository translationRepository) {
        this.tourApiClient = tourApiClient;
        this.eventRepository = eventRepository;
        this.translationRepository = translationRepository;
    }

    // 한국어 공공API에서 전국 축제 데이터를 가져와 DB에 저장
    public void syncAll() {
        // 올해 1월 1일부터 검색 → 현재 진행 중인 축제도 포함
        String startOfYear = LocalDate.now().getYear() + "0101"; // 예: "20260101"
        AREA_CODES.forEach((code, region) -> {
            try {
                var items = tourApiClient.fetchFestivals(code, startOfYear);
                items.forEach(item -> upsert(item, region));
                log.info("한국어 동기화 완료 - {}: {}건", region, items.size());
            } catch (Exception e) {
                log.error("한국어 동기화 실패 - {}", region, e);
            }
        });
    }

    // 영문 공공API에서 번역 데이터를 가져와 event_translation 테이블에 저장
    public void syncEnglish() {
        // 한국어 syncAll() 과 같은 기준 날짜 사용
        String startOfYear = LocalDate.now().getYear() + "0101"; // 예: "20260101"
        AREA_CODES.forEach((code, region) -> {
            try {
                var items = tourApiClient.fetchFestivalsEng(code, startOfYear);
                items.forEach(this::upsertTranslation);
                log.info("영문 동기화 완료 - {}: {}건", region, items.size());
            } catch (Exception e) {
                log.error("영문 동기화 실패 - {}", region, e);
            }
        });
    }

    // 있으면 수정, 없으면 새로 저장 (한국어)
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

    // 영문 번역 저장 또는 업데이트
    private void upsertTranslation(EventItem item) {
        // 영문 API의 contentId는 한국어 API와 달라서 contentId로 매칭하면 항상 실패함
        // 대신 위도+경도가 두 API에서 동일하므로 이걸로 같은 축제를 찾음
        Double lat = parseDouble(item.getMapy());
        Double lng = parseDouble(item.getMapx());

        if (lat == null || lng == null) return;

        eventRepository.findFirstByLatitudeAndLongitude(lat, lng).ifPresent(event -> {
            translationRepository.findByContentIdAndLanguage(event.getContentId(), "en")
                    .ifPresentOrElse(
                            t -> t.updateTitle(item.getTitle()),
                            () -> translationRepository.save(
                                    EventTranslation.create(event.getContentId(), "en", item.getTitle())
                            )
                    );
        });
    }

    // 목록 조회 (지역 필터, 키워드 검색, 언어 지원)
    @Transactional(readOnly = true)
    public Page<EventResponse> getEvents(String region, String keyword, String lang, Pageable pageable) {
        Page<Event> events;

        if (keyword != null && !keyword.isBlank()) {
            events = eventRepository.findByTitleContaining(keyword, pageable);
        } else if (region != null && !region.isBlank()) {
            events = eventRepository.findByRegionContaining(region, pageable);
        } else {
            events = eventRepository.findAll(pageable);
        }

        // 영어 요청이면 번역된 제목으로 교체 (없으면 한국어 그대로 표시)
        if ("en".equals(lang)) {
            return events.map(e -> {
                EventTranslation trans = translationRepository
                        .findByContentIdAndLanguage(e.getContentId(), "en").orElse(null);
                String title = (trans != null && trans.getTitle() != null) ? trans.getTitle() : e.getTitle();
                return EventResponse.fromWithTitle(e, title);
            });
        }

        return events.map(EventResponse::from);
    }

    // 단건 조회 (TourAPI detailCommon2로 소개글·홈페이지 추가, 언어 지원)
    @Transactional(readOnly = true)
    public EventResponse getEvent(Long id, String lang) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("축제를 찾을 수 없습니다. id=" + id));

        // 영어 요청이면 영문 API에서 상세 정보 가져오기
        if ("en".equals(lang)) {
            EventTranslation trans = translationRepository
                    .findByContentIdAndLanguage(event.getContentId(), "en").orElse(null);
            String title = (trans != null && trans.getTitle() != null) ? trans.getTitle() : event.getTitle();

            // 영문 소개글 가져오기 (없으면 한국어로 대체)
            EventDetailDto detail = tourApiClient.fetchDetailEng(event.getContentId());
            if (detail.getOverview() == null) {
                detail = tourApiClient.fetchDetail(event.getContentId());
            }

            return EventResponse.fromWithTitleAndDetail(event, title, detail);
        }

        // 기본: 한국어
        EventDetailDto detail = tourApiClient.fetchDetail(event.getContentId());
        return EventResponse.from(event, detail);
    }

    private Double parseDouble(String value) {
        if (value == null || value.isBlank()) return null;
        try { return Double.parseDouble(value); }
        catch (NumberFormatException e) { return null; }
    }
}
