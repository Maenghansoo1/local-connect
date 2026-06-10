package com.project.project.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupDto dto) {
        try {
            userService.signup(dto);
            return ResponseEntity.ok(Map.of("message", "회원가입이 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        User user = userService.getUser(authentication.getName());
        return ResponseEntity.ok(Map.of("username", user.getUsername(), "nickname", user.getNickname()));
    }

    @PutMapping("/nickname")
    public ResponseEntity<?> updateNickname(@RequestBody Map<String, String> data,
                                            Authentication authentication) {
        try {
            String nickname = data.get("nickname").trim();
            userService.updateNickname(authentication.getName(), nickname);
            return ResponseEntity.ok(Map.of("message", "닉네임이 변경되었습니다.", "nickname", nickname));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
