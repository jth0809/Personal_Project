package com.personal.backend.config.oauth; // 적절한 패키지에 위치

import com.personal.backend.config.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final AppProperties appProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // 이메일로 우리 서비스의 JWT 토큰 생성
        String token = jwtTokenProvider.createToken(email);

        // 프론트엔드로 보낼 리디렉션 URL 생성. 토큰을 쿼리 파라미터로 추가
        String targetUrl = UriComponentsBuilder.fromUriString(appProperties.oauth2().redirectUri()) // 프론트엔드 주소
                .queryParam("token", token)
                .build().toUriString();

        // 생성된 URL로 리디렉션
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}