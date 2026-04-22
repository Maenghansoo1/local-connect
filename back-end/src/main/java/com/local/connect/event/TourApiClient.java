package com.local.connect.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

// 한국관광공사 공공API 호출
@Slf4j
@Component
public class TourApiClient {

    private static final String BASE_URL = "https://apis.data.go.kr/B551011/KorService1/searchFestival1";

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
        try {
            String url = BASE_URL
                    + "?serviceKey=" + apiKey
                    + "&numOfRows=100&pageNo=1"
                    + "&MobileOS=ETC&MobileApp=LocalConnect"
                    + "&_type=json&listYN=Y&arrange=A"
                    + "&areaCode=" + areaCode
                    + "&eventStartDate=" + eventStartDate;

            String response = restClient.get()
                    .uri(URI.create(url))
                    .retrieve()
                    .body(String.class);

            return parseItems(response);
        } catch (Exception e) {
            log.error("TourAPI 호출 실패 - areaCode: {}", areaCode, e);
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
