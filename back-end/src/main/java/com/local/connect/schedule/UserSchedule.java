package com.local.connect.schedule;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

// user_schedule 테이블과 연결되는 클래스
@Entity
@NoArgsConstructor
@Table(name = "user_schedule")
public class UserSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;       // 누가 저장했는지 (users 테이블의 id)
    private Long eventId;      // 어떤 축제인지 (event 테이블의 id)
    private String eventTitle; // 축제 이름
    private String startDate;  // 시작일 (20240501 형식)
    private String endDate;    // 종료일
    private String region;     // 지역
    private String imageUrl;   // 이미지 URL
    private LocalDateTime savedAt; // 저장한 시각

    @PrePersist
    protected void onCreate() {
        this.savedAt = LocalDateTime.now();
    }

    // Getter 메서드들
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getEventId() { return eventId; }
    public String getEventTitle() { return eventTitle; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getRegion() { return region; }
    public String getImageUrl() { return imageUrl; }
    public LocalDateTime getSavedAt() { return savedAt; }

    // 일정 생성 메서드
    public static UserSchedule create(Long userId, Long eventId, String eventTitle,
                                      String startDate, String endDate,
                                      String region, String imageUrl) {
        UserSchedule s = new UserSchedule();
        s.userId = userId;
        s.eventId = eventId;
        s.eventTitle = eventTitle;
        s.startDate = startDate;
        s.endDate = endDate;
        s.region = region;
        s.imageUrl = imageUrl;
        return s;
    }
}
