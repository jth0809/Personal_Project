package com.personal.backend.dto;

import com.personal.backend.domain.User;

public class UserDto {

    /**
     * 클라이언트가 '회원가입'을 요청할 때 사용하는 DTO
     */
    public record SignupRequest(
            String email,
            String password,
            String username
    ) {
        // 비밀번호는 Service 계층에서 암호화된 후 Entity로 변환됩니다.
        public User toEntity(String encodedPassword) {
            return User.builder()
                    .email(email)
                    .password(encodedPassword)
                    .username(username)
                    .build();
        }
    }

    /**
     * 클라이언트가 '로그인'을 요청할 때 사용하는 DTO
     */
    public record LoginRequest(
            String email,
            String password
    ) {}

    /**
     * 로그인 성공 시, 클라이언트에게 Access Token을 반환하기 위한 DTO
     */
    public record TokenResponse(
            String accessToken
    ) {}
}