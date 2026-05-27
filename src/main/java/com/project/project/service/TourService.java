package com.project.project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class TourService {

    private final RestTemplate restTemplate;

    @Value("${tour.api.key}")
    private String apiKey;

    @Value("${tour.api.key.en}")
    private String apiKeyEn;

    // 한국어 contentTypeId → 영문 contentTypeId 변환 테이블
    private static final Map<String, String> KO_TO_EN_TYPE = Map.of(
        "12", "76",  // 관광지 → Tourist Spot
        "14", "78",  // 문화시설 → Culture Facility
        "15", "85",  // 축제 → Festival
        "28", "75",  // 레포츠 → Leisure Sports
        "32", "80",  // 숙박 → Accommodation
        "38", "79",  // 쇼핑 → Shopping
        "39", "82"   // 음식점 → Restaurant
    );

    public TourService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getSpots(String areaCode, String contentTypeId, String lang) {
        boolean isEn = "en".equalsIgnoreCase(lang);
        String baseUrl = isEn
                ? "http://apis.data.go.kr/B551011/EngService2/areaBasedList2"
                : "http://apis.data.go.kr/B551011/KorService2/areaBasedList2";
        String key = isEn ? apiKeyEn : apiKey;
        String typeId = isEn
                ? KO_TO_EN_TYPE.getOrDefault(contentTypeId, "76")
                : contentTypeId;

        String url = baseUrl
                + "?serviceKey=" + key
                + "&MobileOS=ETC"
                + "&MobileApp=TourApp"
                + "&_type=json"
                + "&contentTypeId=" + typeId
                + "&areaCode=" + areaCode
                + "&numOfRows=20"
                + "&pageNo=1";

        return restTemplate.getForObject(url, String.class);
    }
}
