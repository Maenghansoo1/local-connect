package com.project.project.schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// 여행 일정 하나 (날짜 + 제목 + 메모)
public class Schedule {

    private Long id;
    private Long userId;
    private String title;       // 일정 이름 (예: 부산 여행)
    private LocalDate travelDate; // 여행 날짜
    private String memo;        // 간단한 메모
    private LocalDateTime createdAt;

    // 조회 시 장소 목록을 담아서 함께 반환
    private List<ScheduleItem> items;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public LocalDate getTravelDate() { return travelDate; }
    public void setTravelDate(LocalDate travelDate) { this.travelDate = travelDate; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<ScheduleItem> getItems() { return items; }
    public void setItems(List<ScheduleItem> items) { this.items = items; }
}
