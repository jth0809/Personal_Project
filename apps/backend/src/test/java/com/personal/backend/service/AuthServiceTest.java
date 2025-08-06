package com.personal.backend.service;

import com.personal.backend.config.jwt.JwtTokenProvider;
import com.personal.backend.domain.User;
import com.personal.backend.domain.UserRole;
import com.personal.backend.dto.UserDto;
import com.personal.backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("로그인 성공")
    void login_Success() {
        // given
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword";
        String userEmail = "test@user.com";
        UserDto.LoginRequest request = new UserDto.LoginRequest(userEmail, rawPassword);
        
        User foundUser = User.builder()
                .email(userEmail)
                .password(encodedPassword)
                .username("테스트유저")
                .role(UserRole.USER)
                .build();
        
        // Mock 설정
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(foundUser));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(jwtTokenProvider.createToken(userEmail)).thenReturn("dummy.jwt.token");

        // when
        UserDto.TokenResponse response = authService.login(request);

        // then
        assertThat(response.accessToken()).isEqualTo("dummy.jwt.token");
        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(passwordEncoder, times(1)).matches(rawPassword, encodedPassword);
        verify(jwtTokenProvider, times(1)).createToken(userEmail);
    }

    @Test
    @DisplayName("로그인 실패 - 가입되지 않은 이메일")
    void login_Fail_UserNotFound() {
        // given
        String userEmail = "notfound@user.com";
        UserDto.LoginRequest request = new UserDto.LoginRequest(userEmail, "password123");
        
        // Mock 설정: 해당 이메일의 사용자가 없음을 반환
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.login(request);
        });
        
        assertThat(exception.getMessage()).isEqualTo("가입되지 않은 이메일입니다.");
        verify(passwordEncoder, never()).matches(anyString(), anyString()); // 비밀번호 비교 로직은 호출되면 안 됨
        verify(jwtTokenProvider, never()).createToken(anyString()); // 토큰 생성 로직은 호출되면 안 됨
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_Fail_PasswordMismatch() {
        // given
        String rawPassword = "wrongPassword";
        String encodedPassword = "encodedPassword";
        String userEmail = "test@user.com";
        UserDto.LoginRequest request = new UserDto.LoginRequest(userEmail, rawPassword);

        User foundUser = User.builder()
                .email(userEmail)
                .password(encodedPassword)
                .username("테스트유저")
                .role(UserRole.USER)
                .build();

        // Mock 설정
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(foundUser));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false); // 비밀번호가 일치하지 않음

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.login(request);
        });
        
        assertThat(exception.getMessage()).isEqualTo("잘못된 비밀번호입니다.");
        verify(jwtTokenProvider, never()).createToken(anyString()); // 토큰 생성 로직은 호출되면 안 됨
    }
}