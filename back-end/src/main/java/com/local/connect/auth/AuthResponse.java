package com.local.connect.auth;

// 로그인/회원가입 성공 시 반환하는 데이터 (세션 방식으로 변경 — 토큰 없음)
public class AuthResponse {

    private final String email;
    private final String nickname;

    public AuthResponse(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }

    public String getEmail() { return email; }
    public String getNickname() { return nickname; }
}
