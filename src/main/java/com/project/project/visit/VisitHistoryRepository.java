package com.project.project.visit;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface VisitHistoryRepository {

    @Select("SELECT * FROM visit_history WHERE user_id = #{userId} ORDER BY visited_at DESC")
    List<VisitHistory> findByUserIdOrderByVisitedAtDesc(Long userId);

    @Select("SELECT COUNT(*) FROM visit_history WHERE user_id = #{userId} AND content_id = #{contentId}")
    int countByUserIdAndContentId(@Param("userId") Long userId, @Param("contentId") String contentId);

    @Select("SELECT * FROM visit_history WHERE id = #{id}")
    VisitHistory findById(Long id);

    @Insert("INSERT INTO visit_history (user_id, content_id, title, addr, image, visited_at) " +
            "VALUES (#{userId}, #{contentId}, #{title}, #{addr}, #{image}, #{visitedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(VisitHistory history);

    @Delete("DELETE FROM visit_history WHERE id = #{id}")
    void deleteById(Long id);
}
