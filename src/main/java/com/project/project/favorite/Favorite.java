package com.project.project.favorite;

import java.time.LocalDateTime;

public class Favorite {

    private Long id;
    private Long userId;
    private String contentId;
    private String title;
    private String addr;
    private String image;
    private String contentTypeId;
    private String mapx;
    private String mapy;
    private LocalDateTime createdAt;

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
    public String getContentTypeId() { return contentTypeId; }
    public void setContentTypeId(String contentTypeId) { this.contentTypeId = contentTypeId; }
    public String getMapx() { return mapx; }
    public void setMapx(String mapx) { this.mapx = mapx; }
    public String getMapy() { return mapy; }
    public void setMapy(String mapy) { this.mapy = mapy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
