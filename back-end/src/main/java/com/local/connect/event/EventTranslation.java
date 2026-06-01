package com.local.connect.event;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

// 축제 영문 번역을 저장하는 테이블과 연결되는 클래스
@Entity
@NoArgsConstructor
@Table(name = "event_translation")
public class EventTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contentId;   // event 테이블의 content_id (같은 축제)
    private String language;    // 언어 코드 (예: 'en')
    private String title;       // 번역된 제목

    // Getter 메서드들
    public Long getId() { return id; }
    public String getContentId() { return contentId; }
    public String getLanguage() { return language; }
    public String getTitle() { return title; }

    // 새 번역 데이터 만들기
    public static EventTranslation create(String contentId, String language, String title) {
        EventTranslation t = new EventTranslation();
        t.contentId = contentId;
        t.language = language;
        t.title = title;
        return t;
    }

    // 제목 업데이트
    public void updateTitle(String title) {
        this.title = title;
    }
}
