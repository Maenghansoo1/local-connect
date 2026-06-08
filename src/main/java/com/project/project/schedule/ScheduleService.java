package com.project.project.schedule;

import com.project.project.auth.User;
import com.project.project.auth.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleItemRepository scheduleItemRepository;
    private final UserRepository userRepository;

    public ScheduleService(ScheduleRepository scheduleRepository,
                           ScheduleItemRepository scheduleItemRepository,
                           UserRepository userRepository) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleItemRepository = scheduleItemRepository;
        this.userRepository = userRepository;
    }

    // 내 일정 전체 목록 (각 일정에 장소 목록 포함)
    public List<Schedule> getMySchedules(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new IllegalArgumentException("사용자 없음");
        List<Schedule> schedules = scheduleRepository.findByUserId(user.getId());
        // 각 일정마다 장소 목록도 함께 채워줌
        for (Schedule s : schedules) {
            s.setItems(scheduleItemRepository.findByScheduleId(s.getId()));
        }
        return schedules;
    }

    // 일정 상세 (장소 포함)
    public Schedule getDetail(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId);
        if (schedule == null) throw new IllegalArgumentException("일정 없음");
        schedule.setItems(scheduleItemRepository.findByScheduleId(scheduleId));
        return schedule;
    }

    // 새 일정 생성
    public Schedule create(String username, Map<String, String> data) {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new IllegalArgumentException("사용자 없음");

        Schedule schedule = new Schedule();
        schedule.setUserId(user.getId());
        schedule.setTitle(data.get("title"));
        schedule.setTravelDate(LocalDate.parse(data.get("travelDate")));
        schedule.setMemo(data.get("memo") != null ? data.get("memo") : "");
        schedule.setCreatedAt(LocalDateTime.now());
        scheduleRepository.insert(schedule);
        schedule.setItems(List.of()); // 새 일정은 장소 없음
        return schedule;
    }

    // 일정 삭제 (장소도 함께 삭제)
    @Transactional
    public void delete(String username, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId);
        if (schedule == null) throw new IllegalArgumentException("일정 없음");
        User user = userRepository.findByUsername(username);
        if (!schedule.getUserId().equals(user.getId())) throw new IllegalArgumentException("삭제 권한 없음");
        scheduleItemRepository.deleteByScheduleId(scheduleId); // 장소 먼저 삭제
        scheduleRepository.deleteById(scheduleId);             // 일정 삭제
    }

    // 일정에 장소 추가
    public ScheduleItem addItem(String username, Long scheduleId, Map<String, String> data) {
        Schedule schedule = scheduleRepository.findById(scheduleId);
        if (schedule == null) throw new IllegalArgumentException("일정 없음");
        User user = userRepository.findByUsername(username);
        if (!schedule.getUserId().equals(user.getId())) throw new IllegalArgumentException("권한 없음");

        ScheduleItem item = new ScheduleItem();
        item.setScheduleId(scheduleId);
        item.setContentId(data.get("contentId"));
        item.setTitle(data.get("title"));
        item.setAddr(data.get("addr") != null ? data.get("addr") : "");
        item.setVisitTime(data.get("visitTime") != null ? data.get("visitTime") : "");
        item.setMemo(data.get("memo") != null ? data.get("memo") : "");
        item.setItemOrder(scheduleItemRepository.findMaxOrder(scheduleId) + 1); // 마지막 순서 + 1
        scheduleItemRepository.insert(item);
        return item;
    }

    // 일정에서 장소 삭제
    public void deleteItem(String username, Long scheduleId, Long itemId) {
        Schedule schedule = scheduleRepository.findById(scheduleId);
        if (schedule == null) throw new IllegalArgumentException("일정 없음");
        User user = userRepository.findByUsername(username);
        if (!schedule.getUserId().equals(user.getId())) throw new IllegalArgumentException("권한 없음");
        scheduleItemRepository.deleteById(itemId);
    }
}
