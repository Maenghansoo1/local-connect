package com.project.project.favorite;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FavoriteRepository {

    @Select("SELECT * FROM favorites WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Select("SELECT COUNT(*) FROM favorites WHERE user_id = #{userId} AND content_id = #{contentId}")
    int countByUserIdAndContentId(@Param("userId") Long userId, @Param("contentId") String contentId);

    @Delete("DELETE FROM favorites WHERE user_id = #{userId} AND content_id = #{contentId}")
    void deleteByUserIdAndContentId(@Param("userId") Long userId, @Param("contentId") String contentId);

    @Insert("INSERT INTO favorites (user_id, content_id, title, addr, image, content_type_id, mapx, mapy, created_at) " +
            "VALUES (#{userId}, #{contentId}, #{title}, #{addr}, #{image}, #{contentTypeId}, #{mapx}, #{mapy}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Favorite favorite);
}
