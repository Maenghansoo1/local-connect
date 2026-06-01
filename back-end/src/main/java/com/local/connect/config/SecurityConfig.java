package com.local.connect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 세션 저장소 — 로그인 정보를 HTTP 세션에 보관
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ① CSRF 비활성화 — AJAX 요청에서는 불필요
            .csrf(csrf -> csrf.disable())

            // ② 세션 방식으로 변경 (JWT 제거)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

            // ③ 권한 규칙
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()                      // 로그인/회원가입 누구나
                .requestMatchers(HttpMethod.GET, "/api/events/**").permitAll()    // 축제 조회 누구나
                .requestMatchers(HttpMethod.POST, "/api/events/sync").permitAll()    // 동기화 (임시)
                .requestMatchers(HttpMethod.POST, "/api/events/sync/en").permitAll() // 영문 동기화 (임시)
                .requestMatchers("/", "/index.html", "/calendar.html", "/css/**", "/js/**").permitAll() // 정적 파일 누구나
                .anyRequest().authenticated()                                      // 나머지는 로그인 필요
            );

        return http.build();
    }
}
