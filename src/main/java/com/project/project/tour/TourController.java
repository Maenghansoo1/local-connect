package com.project.project.tour;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TourController {

    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @GetMapping("/spots")
    public String getSpots(
            @RequestParam(defaultValue = "1") String areaCode,
            @RequestParam(defaultValue = "12") String contentTypeId,
            @RequestParam(defaultValue = "ko") String lang,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "6") int numOfRows) {
        return tourService.getSpots(areaCode, contentTypeId, lang, pageNo, numOfRows);
    }

    // 축제 목록 — 오늘 기준 가까운 날짜순 (지역 선택 없이도 전국 조회 가능)
    @GetMapping("/spots/festivals")
    public String getFestivals(
            @RequestParam(defaultValue = "") String areaCode,
            @RequestParam(defaultValue = "ko") String lang,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "6") int numOfRows) {
        return tourService.getFestivals(areaCode, lang, pageNo, numOfRows);
    }

    @GetMapping("/spots/common")
    public String getDetailCommon(
            @RequestParam String contentId,
            @RequestParam(defaultValue = "ko") String lang) {
        return tourService.getDetailCommon(contentId, lang);
    }

    @GetMapping("/spots/detail")
    public String getDetailIntro(
            @RequestParam String contentId,
            @RequestParam(defaultValue = "12") String contentTypeId,
            @RequestParam(defaultValue = "ko") String lang) {
        return tourService.getDetailIntro(contentId, contentTypeId, lang);
    }
}
