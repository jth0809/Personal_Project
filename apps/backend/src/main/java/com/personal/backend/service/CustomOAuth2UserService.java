package com.personal.backend.service;

import com.personal.backend.domain.User;
import com.personal.backend.domain.UserRole;
import com.personal.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 기본 OAuth2UserService를 사용하여 사용자 정보 로드
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 소셜 서비스로부터 받은 사용자 정보 추출 (Google 기준)
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        // String provider = userRequest.getClientRegistration().getRegistrationId(); // "google", "kakao" 등

        // DB에서 사용자 조회 또는 신규 생성
        User user = saveOrUpdate(email, name);

        // Spring Security가 사용할 UserDetails 객체를 반환
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                attributes,
                "email" // Principal을 식별하는 데 사용할 속성 키 (Google의 경우 'sub'가 더 좋지만, 여기서는 email 사용)
        );
    }

    private User saveOrUpdate(String email, String name) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            // 필요하다면 여기서 기존 사용자의 이름이나 프로필 사진을 업데이트할 수 있습니다.
        } else {
            // 사용자가 존재하지 않으면 새로 생성 (자동 회원가입)
            user = User.builder()
                    .email(email)
                    .username(name)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString())) // 임의의 값 암호화
                    .role(UserRole.USER)
                    .build();
            userRepository.save(user);
        }
        return user;
    }
}