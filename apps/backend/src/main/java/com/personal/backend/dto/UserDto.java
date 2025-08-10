package com.personal.backend.dto;

import com.personal.backend.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public class UserDto {

    /**
     * 클라이언트가 '회원가입'을 요청할 때 사용하는 DTO
     */
    public record SignupRequest(    
        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        @Size(min = 12, message = "비밀번호는 최소 12자 이상이어야 합니다.")
        String password,

        @NotBlank(message = "사용자 이름은 필수 입력 항목입니다.")
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
        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")    
        String email,
        
        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        @Size(min = 12, message = "비밀번호는 최소 12자 이상이어야 합니다.")
        String password
    ) {}

    /**
     * 로그인 성공 시, 클라이언트에게 Access Token을 반환하기 위한 DTO
     */
    public record TokenResponse(
            String accessToken
    ) {}
}