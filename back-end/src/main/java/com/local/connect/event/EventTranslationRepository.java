package com.local.connect.event;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// event_translation 테이블 조회/저장
public interface EventTranslationRepository extends JpaRepository<EventTranslation, Long> {

    // contentId와 언어 코드로 번역 찾기
    Optional<EventTranslation> findByContentIdAndLanguage(String contentId, String language);

    // 해당 contentId와 언어의 번역이 이미 있는지 확인
    boolean existsByContentIdAndLanguage(String contentId, String language);
}
