package com.personal.backend.service;

import com.personal.backend.config.jwt.JwtTokenProvider;
import com.personal.backend.dto.UserDto;
import com.personal.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화를 위한 의존성
    private final JwtTokenProvider jwtTokenProvider;

    public UserDto.TokenResponse login(UserDto.LoginRequest request) {
        // 1. 이메일로 사용자 확인
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        // 2. 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        // 3. JWT 토큰 생성 및 반환
        String token = jwtTokenProvider.createToken(user.getEmail());
        return new UserDto.TokenResponse(token);
    }
}
