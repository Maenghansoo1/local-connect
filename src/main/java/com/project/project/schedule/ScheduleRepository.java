package com.project.project.schedule;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ScheduleRepository {

    // 내 일정 전체 목록 (날짜 오름차순)
    @Select("SELECT * FROM schedules WHERE user_id = #{userId} ORDER BY travel_date ASC")
    List<Schedule> findByUserId(Long userId);

    // 일정 단건 조회
    @Select("SELECT * FROM schedules WHERE id = #{id}")
    Schedule findById(Long id);

    // 일정 생성
    @Insert("INSERT INTO schedules (user_id, title, travel_date, memo, created_at) " +
            "VALUES (#{userId}, #{title}, #{travelDate}, #{memo}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Schedule schedule);

    // 일정 삭제
    @Delete("DELETE FROM schedules WHERE id = #{id}")
    void deleteById(Long id);
}
