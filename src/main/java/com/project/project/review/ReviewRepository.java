package com.project.project.review;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReviewRepository {

    @Select("SELECT r.*, u.username FROM reviews r JOIN users u ON r.user_id = u.id " +
            "WHERE r.content_id = #{contentId} ORDER BY r.created_at DESC")
    List<Review> findByContentIdOrderByCreatedAtDesc(String contentId);

    @Select("SELECT r.*, u.username FROM reviews r JOIN users u ON r.user_id = u.id " +
            "WHERE r.user_id = #{userId} ORDER BY r.created_at DESC")
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Select("SELECT r.*, u.username FROM reviews r JOIN users u ON r.user_id = u.id WHERE r.id = #{id}")
    Review findById(Long id);

    @Insert("INSERT INTO reviews (user_id, content_id, spot_title, content, rating, created_at) " +
            "VALUES (#{userId}, #{contentId}, #{spotTitle}, #{content}, #{rating}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Review review);

    @Delete("DELETE FROM reviews WHERE id = #{id}")
    void deleteById(Long id);
}
