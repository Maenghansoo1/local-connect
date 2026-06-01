package com.local.connect.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 한국관광공사 공공API 호출
@Component
public class TourApiClient {

    private static final Logger log = LoggerFactory.getLogger(TourApiClient.class);

    private static final String BASE_URL   = "https://apis.data.go.kr/B551011/KorService2/searchFestival2";
    private static final String DETAIL_URL = "https://apis.data.go.kr/B551011/KorService2/detailCommon2";

    // 영문 API (한국어와 같은 contentId 사용, 영문 제목/설명 반환)
    private static final String ENG_BASE_URL   = "https://apis.data.go.kr/B551011/EngService2/searchFestival2";
    private static final String ENG_DETAIL_URL = "https://apis.data.go.kr/B551011/EngService2/detailCommon2";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public TourApiClient(@Value("${api.tour.key}") String apiKey) {
        this.restClient = RestClient.create();
        this.objectMapper = new ObjectMapper();
        this.apiKey = apiKey;
    }

    // 지역 코드와 시작일 기준으로 축제 목록 가져오기
    public List<EventItem> fetchFestivals(String areaCode, String eventStartDate) {
        // 한 번에 최대 500개 요청 (지역별로 500개를 넘는 경우는 거의 없음)
        String url = BASE_URL
                + "?serviceKey=" + apiKey
                + "&numOfRows=500&pageNo=1"
                + "&MobileOS=ETC&MobileApp=LocalConnect"
                + "&_type=json&arrange=A"
                + "&areaCode=" + areaCode
                + "&eventStartDate=" + eventStartDate;
        try {
            String response = restClient.get()
                    .uri(URI.create(url))
                    .retrieve()
                    .body(String.class);
            List<EventItem> items = parseItems(response);
            log.info("TourAPI 한국어 조회 완료 - areaCode={}, {}건", areaCode, items.size());
            return items;
        } catch (Exception e) {
            log.error("TourAPI 호출 실패 - areaCode: {}, 오류: {}", areaCode, e.getMessage());
            return List.of();
        }
    }

    // contentId로 축제 상세 정보(소개글, 홈페이지) 가져오기
    public EventDetailDto fetchDetail(String contentId) {
        String url = DETAIL_URL
                + "?serviceKey=" + apiKey
                + "&contentId=" + contentId
                + "&MobileOS=ETC&MobileApp=LocalConnect&_type=json";
        try {
            String response = restClient.get()
                    .uri(URI.create(url))
                    .retrieve()
                    .body(String.class);
            return parseDetail(response);
        } catch (Exception e) {
            log.error("detailCommon2 호출 실패 - contentId: {}, 오류: {}", contentId, e.getMessage());
            return new EventDetailDto(null, null);
        }
    }

    // 영문 API에서 지역별 축제 목록 가져오기
    public List<EventItem> fetchFestivalsEng(String areaCode, String eventStartDate) {
        // 한 번에 최대 500개 요청
        String url = ENG_BASE_URL
                + "?serviceKey=" + apiKey
                + "&numOfRows=500&pageNo=1"
                + "&MobileOS=ETC&MobileApp=LocalConnect"
                + "&_type=json&arrange=A"
                + "&areaCode=" + areaCode
                + "&eventStartDate=" + eventStartDate;
        try {
            String response = restClient.get()
                    .uri(URI.create(url))
                    .retrieve()
                    .body(String.class);
            List<EventItem> items = parseItems(response);
            log.info("TourAPI 영문 조회 완료 - areaCode={}, {}건", areaCode, items.size());
            return items;
        } catch (Exception e) {
            log.error("영문 TourAPI 호출 실패 - areaCode: {}, 오류: {}", areaCode, e.getMessage());
            return List.of();
        }
    }

    // 영문 API에서 상세 정보(소개글) 가져오기
    public EventDetailDto fetchDetailEng(String contentId) {
        String url = ENG_DETAIL_URL
                + "?serviceKey=" + apiKey
                + "&contentId=" + contentId
                + "&MobileOS=ETC&MobileApp=LocalConnect&_type=json";
        try {
            String response = restClient.get()
                    .uri(URI.create(url))
                    .retrieve()
                    .body(String.class);
            return parseDetail(response);
        } catch (Exception e) {
            log.error("영문 detailCommon2 호출 실패 - contentId: {}, 오류: {}", contentId, e.getMessage());
            return new EventDetailDto(null, null);
        }
    }

    private EventDetailDto parseDetail(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode item = root.path("response").path("body").path("items").path("item");
            JsonNode node = item.isArray() ? item.get(0) : item;
            if (node == null || node.isMissingNode() || node.isNull()) {
                return new EventDetailDto(null, null);
            }
            String overview = node.path("overview").asText(null);
            String homepage = extractUrl(node.path("homepage").asText(null));
            return new EventDetailDto(
                    (overview != null && !overview.isBlank()) ? overview : null,
                    homepage
            );
        } catch (Exception e) {
            log.error("detailCommon2 파싱 실패: {}", e.getMessage());
            return new EventDetailDto(null, null);
        }
    }

    // TourAPI homepage 필드는 "<a href='url'>...</a>" 형태로 올 수 있음 → URL만 추출
    private String extractUrl(String homepage) {
        if (homepage == null || homepage.isBlank()) return null;
        Matcher m = Pattern.compile("href=[\"']([^\"']+)[\"']").matcher(homepage);
        if (m.find()) return m.group(1);
        return homepage.trim();
    }

    private List<EventItem> parseItems(String json) throws Exception {
        JsonNode root = objectMapper.readTree(json);
        JsonNode items = root.path("response").path("body").path("items").path("item");

        List<EventItem> result = new ArrayList<>();
        if (items.isArray()) {
            for (JsonNode node : items) {
                result.add(objectMapper.treeToValue(node, EventItem.class));
            }
        } else if (items.isObject()) {
            result.add(objectMapper.treeToValue(items, EventItem.class));
        }
        return result;
    }
}
