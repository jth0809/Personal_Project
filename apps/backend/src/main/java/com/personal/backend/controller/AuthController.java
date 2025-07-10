package com.personal.backend.controller;

import com.personal.backend.dto.UserDto;
import com.personal.backend.service.AuthService;
import com.personal.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    /**
     * 회원가입을 처리하는 API
     * POST /api/auth/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserDto.SignupRequest request) {
        userService.signup(request);
        return ResponseEntity.ok("회원가입이 성공적으로 완료되었습니다.");
    }

    /**
     * 로그인을 처리하고 JWT 토큰을 발급하는 API
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<UserDto.TokenResponse> login(@RequestBody UserDto.LoginRequest request) {
        UserDto.TokenResponse token = authService.login(request);
        return ResponseEntity.ok(token); // 생성된 토큰을 응답으로 반환합니다.
    }
}
