package com.project.project.controller;

import com.project.project.service.TourService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}