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

// 한국관광공사 공공API 호출
@Component
public class TourApiClient {

    private static final Logger log = LoggerFactory.getLogger(TourApiClient.class);

    private static final String BASE_URL = "https://apis.data.go.kr/B551011/KorService2/searchFestival2";

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
        String url = BASE_URL
                + "?serviceKey=" + apiKey
                + "&numOfRows=100&pageNo=1"
                + "&MobileOS=ETC&MobileApp=LocalConnect"
                + "&_type=json&arrange=A"
                + "&areaCode=" + areaCode
                + "&eventStartDate=" + eventStartDate;
        log.info("TourAPI 호출: {}", url.replace(apiKey, apiKey.substring(0, 8) + "..."));
        try {
            String response = restClient.get()
                    .uri(URI.create(url))
                    .retrieve()
                    .body(String.class);

            log.info("TourAPI 응답 (areaCode={}): {}", areaCode,
                    response != null && response.length() > 200 ? response.substring(0, 200) : response);
            return parseItems(response);
        } catch (Exception e) {
            log.error("TourAPI 호출 실패 - areaCode: {}, 오류: {}", areaCode, e.getMessage());
            return List.of();
        }
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
