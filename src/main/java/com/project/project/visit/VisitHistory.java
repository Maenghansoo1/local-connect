package com.project.project.visit;

import java.time.LocalDateTime;

public class VisitHistory {

    private Long id;
    private Long userId;
    private String contentId;
    private String title;
    private String addr;
    private String image;
    private LocalDateTime visitedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getContentId() { return contentId; }
    public void setContentId(String contentId) { this.contentId = contentId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAddr() { return addr; }
    public void setAddr(String addr) { this.addr = addr; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public LocalDateTime getVisitedAt() { return visitedAt; }
    public void setVisitedAt(LocalDateTime visitedAt) { this.visitedAt = visitedAt; }
}
