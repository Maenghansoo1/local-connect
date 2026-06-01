package com.local.connect.schedule;

// 일정 저장 요청 DTO — 어떤 축제를, 어떤 날짜로 저장할지 받는 클래스
public class ScheduleRequest {

    private Long eventId;     // 저장할 축제 ID
    private String startDate; // 사용자가 선택한 시작일 (없으면 축제 날짜 사용)
    private String endDate;   // 사용자가 선택한 종료일 (없으면 축제 날짜 사용)

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
}
