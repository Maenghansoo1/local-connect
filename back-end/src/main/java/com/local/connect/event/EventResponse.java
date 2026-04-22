package com.local.connect.event;

import lombok.Getter;

// 축제 조회 시 클라이언트에 반환하는 데이터
@Getter
public class EventResponse {

    private final Long id;
    private final String contentId;
    private final String title;
    private final String startDate;
    private final String endDate;
    private final String address;
    private final String region;
    private final String imageUrl;
    private final String tel;
    private final Double latitude;
    private final Double longitude;

    public EventResponse(Long id, String contentId, String title, String startDate,
                         String endDate, String address, String region,
                         String imageUrl, String tel, Double latitude, Double longitude) {
        this.id = id;
        this.contentId = contentId;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.address = address;
        this.region = region;
        this.imageUrl = imageUrl;
        this.tel = tel;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static EventResponse from(Event e) {
        return new EventResponse(
                e.getId(), e.getContentId(), e.getTitle(),
                e.getStartDate(), e.getEndDate(), e.getAddress(),
                e.getRegion(), e.getImageUrl(), e.getTel(),
                e.getLatitude(), e.getLongitude()
        );
    }
}
