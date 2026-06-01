package com.project.project.tour;

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

    private static final Map<String, String> KO_TO_EN_TYPE = Map.of(
        "12", "76",
        "14", "78",
        "15", "85",
        "28", "75",
        "32", "80",
        "38", "79",
        "39", "82"
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
        String typeId = isEn ? KO_TO_EN_TYPE.getOrDefault(contentTypeId, "76") : contentTypeId;

        String url = baseUrl
                + "?serviceKey=" + key
                + "&MobileOS=ETC&MobileApp=TourApp&_type=json"
                + "&contentTypeId=" + typeId
                + "&areaCode=" + areaCode
                + "&numOfRows=20&pageNo=1";

        return restTemplate.getForObject(url, String.class);
    }

    public String getDetailIntro(String contentId, String contentTypeId, String lang) {
        boolean isEn = "en".equalsIgnoreCase(lang);
        String baseUrl = isEn
                ? "http://apis.data.go.kr/B551011/EngService2/detailIntro2"
                : "http://apis.data.go.kr/B551011/KorService2/detailIntro2";
        String key = isEn ? apiKeyEn : apiKey;
        String typeId = isEn ? KO_TO_EN_TYPE.getOrDefault(contentTypeId, contentTypeId) : contentTypeId;

        String url = baseUrl
                + "?serviceKey=" + key
                + "&MobileOS=ETC&MobileApp=TourApp&_type=json"
                + "&contentId=" + contentId
                + "&contentTypeId=" + typeId;

        return restTemplate.getForObject(url, String.class);
    }
}
