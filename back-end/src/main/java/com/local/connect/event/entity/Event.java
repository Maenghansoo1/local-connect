package com.local.connect.event.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String contentId;

    private String title;
    private String startDate;
    private String endDate;
    private String address;
    private String region;
    private String imageUrl;
    private String tel;
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Insert용 정적 팩토리
    public static Event create(String contentId, String title, String startDate, String endDate,
                               String address, String region, String imageUrl, String tel) {
        Event event = new Event();
        event.contentId = contentId;
        event.title = title;
        event.startDate = startDate;
        event.endDate = endDate;
        event.address = address;
        event.region = region;
        event.imageUrl = imageUrl;
        event.tel = tel;
        return event;
    }

    // Update용 메서드
    public void update(String title, String startDate, String endDate,
                       String address, String region, String imageUrl, String tel) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.address = address;
        this.region = region;
        this.imageUrl = imageUrl;
        this.tel = tel;
    }
}
