package com.project.project.auth;

import org.apache.ibatis.annotations.*;

@Mapper
public interface UserRepository {

    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    int countByUsername(String username);

    @Select("SELECT COUNT(*) FROM users WHERE email = #{email}")
    int countByEmail(String email);

    @Insert("INSERT INTO users (username, password, nickname, email, provider, provider_id, created_at) " +
            "VALUES (#{username}, #{password}, #{nickname}, #{email}, #{provider}, #{providerId}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);
}
