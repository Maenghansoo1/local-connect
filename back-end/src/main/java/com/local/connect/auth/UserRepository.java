package com.local.connect.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// users 테이블에 쿼리하는 인터페이스
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}
