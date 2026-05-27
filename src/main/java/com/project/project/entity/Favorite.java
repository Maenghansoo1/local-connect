package com.project.project.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorites")
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String contentId;      // 관광지 고유 ID

    @Column(nullable = false)
    private String title;          // 관광지 이름

    private String addr;           // 주소
    private String image;          // 이미지 URL
    private String contentTypeId;  // 카테고리
    private String mapx;           // 경도
    private String mapy;           // 위도

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() { this.createdAt = LocalDateTime.now(); }

    // Getters & Setters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
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
}
