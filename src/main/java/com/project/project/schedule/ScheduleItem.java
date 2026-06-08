package com.project.project.schedule;

// 일정 안에 포함된 장소 하나
public class ScheduleItem {

    private Long id;
    private Long scheduleId;    // 어떤 일정에 속하는지
    private String contentId;   // 관광지 ID
    private String title;       // 장소 이름
    private String addr;        // 주소
    private String visitTime;   // 방문 시간 (예: "10:00")
    private int itemOrder;      // 순서 (1, 2, 3...)
    private String memo;        // 메모

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getScheduleId() { return scheduleId; }
    public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
    public String getContentId() { return contentId; }
    public void setContentId(String contentId) { this.contentId = contentId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAddr() { return addr; }
    public void setAddr(String addr) { this.addr = addr; }
    public String getVisitTime() { return visitTime; }
    public void setVisitTime(String visitTime) { this.visitTime = visitTime; }
    public int getItemOrder() { return itemOrder; }
    public void setItemOrder(int itemOrder) { this.itemOrder = itemOrder; }
    public String getMemo() { return memo; }
    public void setMemo(String memo) { this.memo = memo; }
}
