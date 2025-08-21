package com.personal.backend.service;

import com.personal.backend.domain.User;
import com.personal.backend.domain.UserRole;
import com.personal.backend.dto.UserDto;
import com.personal.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(UserDto.SignupRequest request) {
        // 이메일 중복 확인
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.password());

        // DTO를 Entity로 변환하여 저장
        User newUser = User.builder()
                .email(request.email())
                .password(encodedPassword)
                .username(request.username())
                .role(UserRole.USER)
                .build();
        
        userRepository.save(newUser);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findUsersByEmailIn(Set<String> emails) {
        return userRepository.findByEmailIn(emails);
    }
}
