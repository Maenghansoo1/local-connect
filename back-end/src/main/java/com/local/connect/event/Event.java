package com.local.connect.event;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// DB의 event 테이블과 연결되는 클래스
@Entity
@Getter
@NoArgsConstructor
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String contentId;  // 공공API 고유 ID

    private String title;      // 축제명
    private String startDate;  // 시작일 (예: 20240501)
    private String endDate;    // 종료일
    private String address;    // 주소
    private String region;     // 지역명 (예: 서울)
    private String imageUrl;   // 대표 이미지
    private String tel;        // 전화번호
    private Double latitude;   // 위도
    private Double longitude;  // 경도
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static Event create(String contentId, String title, String startDate, String endDate,
                               String address, String region, String imageUrl, String tel,
                               Double latitude, Double longitude) {
        Event e = new Event();
        e.contentId = contentId;
        e.title = title;
        e.startDate = startDate;
        e.endDate = endDate;
        e.address = address;
        e.region = region;
        e.imageUrl = imageUrl;
        e.tel = tel;
        e.latitude = latitude;
        e.longitude = longitude;
        return e;
    }

    public void update(String title, String startDate, String endDate,
                       String address, String region, String imageUrl, String tel,
                       Double latitude, Double longitude) {
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
}
