package com.personal.backend.service;

import com.personal.backend.domain.User;
import com.personal.backend.domain.UserRole;
import com.personal.backend.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority; // Import if needed
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 정보 로드 성공 - USER 권한")
    void loadUserByUsername_Success_RoleUser() {
        // given: 'USER' 역할을 가진 가짜 사용자 객체
        String userEmail = "test@user.com";
        User dummyUser = User.builder()
                .email(userEmail)
                .password("encodedPassword")
                .username("테스트유저")
                .role(UserRole.USER)
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(dummyUser));

        // when: 테스트 대상 메소드 호출
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);

        // then: 반환된 UserDetails 객체의 정보가 정확한지 확인
        assertThat(userDetails.getUsername()).isEqualTo(userEmail);
        assertThat(userDetails.getPassword()).isEqualTo("encodedPassword");

        // 👇 [수정] 권한 객체에서 문자열(String)을 추출하여 비교합니다.
        assertThat(userDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER");
    }

    @Test
    @DisplayName("사용자 정보 로드 성공 - ADMIN 권한")
    void loadUserByUsername_Success_RoleAdmin() {
        // given: 'ADMIN' 역할을 가진 가짜 사용자 객체
        String adminEmail = "admin@user.com";
        User dummyAdmin = User.builder()
                .email(adminEmail)
                .password("encodedAdminPassword")
                .username("관리자")
                .role(UserRole.ADMIN)
                .build();

        when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.of(dummyAdmin));

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(adminEmail);

        // then: 권한 정보가 'ROLE_ADMIN'으로 정확히 변환되었는지 확인
        // 👇 [수정] 여기도 마찬가지로 문자열을 추출하여 비교합니다.
        assertThat(userDetails.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");
    }


    @Test
    @DisplayName("사용자 정보 로드 실패 - 사용자를 찾을 수 없음")
    void loadUserByUsername_Fail_UserNotFound() {
        // given: 존재하지 않는 이메일
        String notFoundEmail = "notfound@user.com";

        when(userRepository.findByEmail(notFoundEmail)).thenReturn(Optional.empty());

        // when & then: UsernameNotFoundException 예외가 발생하는지 확인
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(notFoundEmail);
        });
    }
}