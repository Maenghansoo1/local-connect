package com.local.connect.event;

// TourAPI detailCommon2 응답에서 꺼낸 상세 정보
public class EventDetailDto {

    private final String overview;
    private final String homepage;

    public EventDetailDto(String overview, String homepage) {
        this.overview = overview;
        this.homepage = homepage;
    }

    public String getOverview() { return overview; }
    public String getHomepage() { return homepage; }
}
