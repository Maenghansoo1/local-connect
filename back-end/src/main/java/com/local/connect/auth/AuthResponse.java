package com.local.connect.auth;

import lombok.Getter;

// 로그인/회원가입 성공 시 반환하는 데이터
@Getter
public class AuthResponse {

    private final String token;    // JWT 토큰
    private final String email;
    private final String nickname;

    public AuthResponse(String token, String email, String nickname) {
        this.token = token;
        this.email = email;
        this.nickname = nickname;
    }
}
