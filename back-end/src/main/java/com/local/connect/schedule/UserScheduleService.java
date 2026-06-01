package com.local.connect.schedule;

import com.local.connect.auth.User;
import com.local.connect.auth.UserRepository;
import com.local.connect.event.Event;
import com.local.connect.event.EventRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// 일정 저장/삭제/조회 비즈니스 로직
@Service
public class UserScheduleService {

    private final UserScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public UserScheduleService(UserScheduleRepository scheduleRepository,
                               UserRepository userRepository,
                               EventRepository eventRepository) {
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    // 일정 저장
    public ScheduleResponse saveSchedule(String email, Long eventId,
                                         String selectedStart, String selectedEnd) {
        // 로그인한 유저 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 저장할 축제 찾기
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("축제를 찾을 수 없습니다."));

        // 이미 저장한 일정이면 오류
        if (scheduleRepository.existsByUserIdAndEventId(user.getId(), eventId)) {
            throw new IllegalArgumentException("이미 저장된 일정입니다.");
        }

        // 사용자가 날짜를 선택했으면 그걸 쓰고, 없으면 축제 기간 그대로 사용
        String startDate = (selectedStart != null && !selectedStart.isEmpty())
                ? selectedStart : event.getStartDate();
        String endDate = (selectedEnd != null && !selectedEnd.isEmpty())
                ? selectedEnd : event.getEndDate();

        // 일정 저장
        UserSchedule schedule = UserSchedule.create(
                user.getId(),
                event.getId(),
                event.getTitle(),
                startDate,
                endDate,
                event.getRegion(),
                event.getImageUrl()
        );
        scheduleRepository.save(schedule);

        return new ScheduleResponse(schedule);
    }

    // 일정 삭제
    public void deleteSchedule(Long scheduleId, String email) {
        // 삭제할 일정 찾기
        UserSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

        // 내 일정인지 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        if (!schedule.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("본인의 일정만 삭제할 수 있습니다.");
        }

        scheduleRepository.delete(schedule);
    }

    // 내 일정 목록 조회
    public List<ScheduleResponse> getMySchedules(String email) {
        // 로그인한 유저 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 내 일정 목록
        List<UserSchedule> schedules = scheduleRepository.findByUserId(user.getId());

        // 응답 형식으로 변환
        List<ScheduleResponse> result = new ArrayList<>();
        for (UserSchedule s : schedules) {
            result.add(new ScheduleResponse(s));
        }
        return result;
    }
}
