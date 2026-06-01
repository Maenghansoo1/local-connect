package com.local.connect.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// user_schedule 테이블에 쿼리하는 인터페이스
public interface UserScheduleRepository extends JpaRepository<UserSchedule, Long> {

    // 특정 유저의 일정 목록 조회
    List<UserSchedule> findByUserId(Long userId);

    // 이미 저장한 일정인지 확인 (중복 방지)
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
}
