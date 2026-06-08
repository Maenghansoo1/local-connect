package com.project.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 정적 리소스 & 공개 API
                .requestMatchers("/", "/index.html", "/css/**", "/js/**").permitAll()
                // 관광지 조회 — 비로그인 허용
                .requestMatchers("/api/spots/**").permitAll()
                // 인증 — signup/login/logout 은 비로그인 허용, me 는 로그인 필요
                .requestMatchers("/api/auth/signup", "/api/auth/login", "/api/auth/logout").permitAll()
                .requestMatchers("/api/auth/me").authenticated()
                // 리뷰 목록 조회 — 비로그인 허용, 나머지(작성/삭제/내리뷰)는 로그인 필요
                .requestMatchers("/api/reviews/spot/**").permitAll()
                // 즐겨찾기 여부 확인 — 비로그인도 false 반환
                .requestMatchers("/api/favorites/check").permitAll()
                // 그 외 모든 요청 — 로그인 필요
                .anyRequest().authenticated()
            )
            // 미인증 요청이 보호된 엔드포인트에 접근 시 JSON 401 반환
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(401);
                    response.getWriter().write("{\"message\":\"로그인이 필요합니다.\"}");
                })
            )
            .formLogin(form -> form
                .loginProcessingUrl("/api/auth/login")
                .failureHandler((request, response, exception) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(401);
                    response.getWriter().write("{\"message\":\"아이디 또는 비밀번호가 틀렸습니다.\"}");
                })
                .successHandler((request, response, authentication) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"message\":\"로그인 성공\",\"username\":\""
                            + authentication.getName() + "\"}");
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"message\":\"로그아웃 되었습니다.\"}");
                })
                .permitAll()
            );

        return http.build();
    }
}
