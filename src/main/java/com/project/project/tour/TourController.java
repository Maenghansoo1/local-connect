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
            @RequestParam(defaultValue = "ko") String lang) {
        return tourService.getSpots(areaCode, contentTypeId, lang);
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
