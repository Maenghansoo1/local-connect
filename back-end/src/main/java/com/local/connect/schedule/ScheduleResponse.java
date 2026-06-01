package com.local.connect.schedule;

// 일정 조회 응답 DTO — 프론트에 내려줄 데이터 형태
public class ScheduleResponse {

    private Long id;
    private Long eventId;
    private String eventTitle;
    private String startDate;
    private String endDate;
    private String region;
    private String imageUrl;

    // UserSchedule 엔티티를 응답 형식으로 변환
    public ScheduleResponse(UserSchedule s) {
        this.id = s.getId();
        this.eventId = s.getEventId();
        this.eventTitle = s.getEventTitle();
        this.startDate = s.getStartDate();
        this.endDate = s.getEndDate();
        this.region = s.getRegion();
        this.imageUrl = s.getImageUrl();
    }

    // Getter 메서드들
    public Long getId() { return id; }
    public Long getEventId() { return eventId; }
    public String getEventTitle() { return eventTitle; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getRegion() { return region; }
    public String getImageUrl() { return imageUrl; }
}
