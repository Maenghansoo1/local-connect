package com.local.connect.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// event 테이블에 쿼리하는 인터페이스
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByContentId(String contentId);
    Page<Event> findByRegionContaining(String region, Pageable pageable);
    Page<Event> findByTitleContaining(String keyword, Pageable pageable);
    // 영문 API는 contentId가 달라서 위도+경도로 같은 축제를 찾음
    Optional<Event> findFirstByLatitudeAndLongitude(Double latitude, Double longitude);
}
