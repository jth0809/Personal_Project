package com.personal.backend.service;

import com.personal.backend.domain.User;
import com.personal.backend.dto.UserDto;
import com.personal.backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // JUnit5에서 Mockito를 사용하기 위한 확장
class UserServiceTest {

    @InjectMocks // 테스트 대상 클래스. @Mock으로 선언된 객체들이 여기에 주입됩니다.
    private UserService userService;

    @Mock // 가짜(Mock) 객체로 만들 의존성
    private UserRepository userRepository;

    @Mock // 가짜(Mock) 객체로 만들 의존성
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signup_Success() {
        // given: 회원가입 요청 데이터가 주어집니다.
        UserDto.SignupRequest request = new UserDto.SignupRequest("test@example.com", "password123", "테스트유저");

        // given-2: userRepository.findByEmail이 호출되면, 이메일이 중복되지 않았음을 의미하는 '빈 Optional'을 반환하도록 설정합니다.
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // given-3: passwordEncoder.encode가 호출되면, 암호화된 비밀번호를 반환하도록 설정합니다.
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // when: 실제 테스트 대상 메소드(userService.signup)를 호출합니다.
        userService.signup(request);

        // then: userRepository.save 메소드가 정확히 1번 호출되었는지 검증합니다.
        // ArgumentCaptor를 사용하면 save 메소드에 어떤 User 객체가 전달되었는지 확인할 수 있습니다.
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        // then-2: 저장된 User 객체의 필드들이 예상대로 설정되었는지 검증합니다.
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo(request.email());
        assertThat(savedUser.getUsername()).isEqualTo(request.username());
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword"); // 암호화된 비밀번호가 저장되었는지 확인
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 이메일 중복")
    void signup_Fail_WhenEmailIsDuplicated() {
        // given: 회원가입 요청 데이터가 주어집니다.
        UserDto.SignupRequest request = new UserDto.SignupRequest("test@example.com", "password123", "테스트유저");

        // given-2: userRepository.findByEmail이 호출되면, 이미 사용자가 존재함을 의미하는 'User 객체가 담긴 Optional'을 반환하도록 설정합니다.
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(User.builder().build()));

        // when & then: userService.signup을 호출했을 때 IllegalArgumentException이 발생하는지 검증합니다.
        assertThrows(IllegalArgumentException.class, () -> {
            userService.signup(request);
        });

        // then-2: 이메일이 중복되었으므로, save 메소드는 절대로 호출되면 안 됩니다.
        verify(userRepository, never()).save(any(User.class));
    }
}