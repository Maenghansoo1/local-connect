package com.local.connect.auth;

// 로그인/회원가입 성공 시 반환하는 데이터
public class AuthResponse {

    private final String token;    // JWT 토큰
    private final String email;
    private final String nickname;

    public AuthResponse(String token, String email, String nickname) {
        this.token = token;
        this.email = email;
        this.nickname = nickname;
    }

    // Getter 메서드들
    public String getToken() { return token; }
    public String getEmail() { return email; }
    public String getNickname() { return nickname; }
}
