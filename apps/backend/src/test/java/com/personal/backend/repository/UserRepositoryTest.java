package com.personal.backend.repository;

import com.personal.backend.domain.User;
import com.personal.backend.domain.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@DataJpaTest // JPA 관련 컴포넌트만 테스트하도록 설정합니다.
@ActiveProfiles("test") // application-test.properties를 사용합니다.
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 저장 및 조회 테스트")
    void saveAndFindUserTest() {
        // given: 이런 사용자를 만들어서
        User user = User.builder()
                .email("testuser@example.com")
                .password("password123") // 실제 환경에서는 암호화 필요
                .username("테스트유저")
                .role(UserRole.USER)
                .build();

        // when: 데이터베이스에 저장하고, 다시 ID로 조회하면
        User savedUser = userRepository.save(user);
        User foundUser = userRepository.findById(savedUser.getId()).orElse(null);

        // then: 저장된 사용자와 조회된 사용자는 동일해야 합니다.
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo(savedUser.getEmail());
        assertThat(foundUser.getUsername()).isEqualTo(savedUser.getUsername());
        assertThat(foundUser.getRole()).isEqualTo(savedUser.getRole());
    }

    @Test
    @DisplayName("이메일로 사용자 조회 테스트")
    void findByEmailTest() {
        // given: 특정 이메일을 가진 사용자를 저장합니다.
        String email = "find@example.com";
        User user = User.builder()
                .email(email)
                .password("password")
                .username("조회용유저")
                .role(UserRole.USER)
                .build();
        userRepository.save(user);

        // when: 해당 이메일로 사용자를 조회합니다.
        Optional<User> foundUserOptional = userRepository.findByEmail(email);

        // then: 조회된 사용자가 존재하며 이메일이 일치해야 합니다.
        assertThat(foundUserOptional).isPresent();
        assertThat(foundUserOptional.get().getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 사용자 조회 시 빈 결과 반환 테스트")
    void findByEmailWhenNotExistsTest() {
        // given: 데이터베이스에 존재하지 않는 이메일 문자열
        String nonExistentEmail = "notfound@example.com";

        // when: 존재하지 않는 이메일로 사용자를 조회합니다.
        Optional<User> foundUserOptional = userRepository.findByEmail(nonExistentEmail);

        // then: 조회 결과는 비어 있어야 합니다.
        assertThat(foundUserOptional).isEmpty();
    }
}