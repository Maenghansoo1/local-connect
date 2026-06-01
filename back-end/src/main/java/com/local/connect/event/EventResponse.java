package com.local.connect.event;

// 축제 조회 시 클라이언트에 반환하는 데이터
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
    private final String overview;
    private final String homepage;

    public EventResponse(Long id, String contentId, String title, String startDate,
                         String endDate, String address, String region,
                         String imageUrl, String tel, Double latitude, Double longitude,
                         String overview, String homepage) {
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
        this.overview = overview;
        this.homepage = homepage;
    }

    // Getter 메서드들
    public Long getId() { return id; }
    public String getContentId() { return contentId; }
    public String getTitle() { return title; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getAddress() { return address; }
    public String getRegion() { return region; }
    public String getImageUrl() { return imageUrl; }
    public String getTel() { return tel; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getOverview() { return overview; }
    public String getHomepage() { return homepage; }

    // 목록 조회용 (overview/homepage 없음)
    public static EventResponse from(Event e) {
        return new EventResponse(
                e.getId(), e.getContentId(), e.getTitle(),
                e.getStartDate(), e.getEndDate(), e.getAddress(),
                e.getRegion(), e.getImageUrl(), e.getTel(),
                e.getLatitude(), e.getLongitude(), null, null
        );
    }

    // 상세 조회용 (overview/homepage 포함)
    public static EventResponse from(Event e, EventDetailDto detail) {
        return new EventResponse(
                e.getId(), e.getContentId(), e.getTitle(),
                e.getStartDate(), e.getEndDate(), e.getAddress(),
                e.getRegion(), e.getImageUrl(), e.getTel(),
                e.getLatitude(), e.getLongitude(),
                detail.getOverview(), detail.getHomepage()
        );
    }

    // 영문 목록 조회용 — 제목만 번역본으로 교체
    public static EventResponse fromWithTitle(Event e, String title) {
        return new EventResponse(
                e.getId(), e.getContentId(), title,
                e.getStartDate(), e.getEndDate(), e.getAddress(),
                e.getRegion(), e.getImageUrl(), e.getTel(),
                e.getLatitude(), e.getLongitude(), null, null
        );
    }

    // 영문 상세 조회용 — 제목 + 소개글 번역본으로 교체
    public static EventResponse fromWithTitleAndDetail(Event e, String title, EventDetailDto detail) {
        return new EventResponse(
                e.getId(), e.getContentId(), title,
                e.getStartDate(), e.getEndDate(), e.getAddress(),
                e.getRegion(), e.getImageUrl(), e.getTel(),
                e.getLatitude(), e.getLongitude(),
                detail.getOverview(), detail.getHomepage()
        );
    }
}
