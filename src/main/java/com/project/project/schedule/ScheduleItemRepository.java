package com.project.project.schedule;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ScheduleItemRepository {

    // 일정에 속한 장소 목록 (순서대로)
    @Select("SELECT * FROM schedule_items WHERE schedule_id = #{scheduleId} ORDER BY item_order ASC")
    List<ScheduleItem> findByScheduleId(Long scheduleId);

    // 현재 일정의 마지막 순서 번호 조회 (새 장소 추가 시 순서 결정용)
    @Select("SELECT COALESCE(MAX(item_order), 0) FROM schedule_items WHERE schedule_id = #{scheduleId}")
    int findMaxOrder(Long scheduleId);

    // 장소 추가
    @Insert("INSERT INTO schedule_items (schedule_id, content_id, title, addr, visit_time, item_order, memo) " +
            "VALUES (#{scheduleId}, #{contentId}, #{title}, #{addr}, #{visitTime}, #{itemOrder}, #{memo})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ScheduleItem item);

    // 장소 단건 삭제
    @Delete("DELETE FROM schedule_items WHERE id = #{id}")
    void deleteById(Long id);

    // 일정 삭제 시 해당 일정의 장소 전체 삭제
    @Delete("DELETE FROM schedule_items WHERE schedule_id = #{scheduleId}")
    void deleteByScheduleId(Long scheduleId);
}
