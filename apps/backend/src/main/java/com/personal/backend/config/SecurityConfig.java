package com.personal.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.personal.backend.config.jwt.JwtAuthenticationFilter;
import com.personal.backend.config.jwt.JwtTokenProvider;
import com.personal.backend.config.oauth.OAuth2AuthenticationSuccessHandler;
import com.personal.backend.service.CustomOAuth2UserService;

@Configuration // 이 클래스가 설정 파일임을 스프링에게 알려줍니다.
@EnableWebSecurity // 스프링 시큐리티의 웹 보안 기능을 활성화합니다.
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    /**
     * HTTP 요청에 대한 보안 규칙을 설정합니다.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF(Cross-Site Request Forgery) 보호를 비활성화합니다.
                //    REST API는 보통 세션이 아닌 토큰 방식을 사용하므로 필요하지 않습니다.
                .csrf(csrf -> csrf.disable())

                // 2. 세션을 사용하지 않고, 상태 없는(stateless) 방식으로 운영합니다.
                //    모든 인증은 요청 헤더의 JWT 토큰으로 처리합니다.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. 각 URL 경로별로 접근 권한을 설정합니다.
                .authorizeHttpRequests(authz -> authz
                        // "/api/auth/**" (로그인, 회원가입) 경로는 누구나 접근 가능
                        .requestMatchers("/**").permitAll()
                        // "GET /api/products/**" (상품 조회) 경로는 누구나 접근 가능
                        //.requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        //.requestMatchers(HttpMethod.GET, "/swagger-ui.html").permitAll()
                        //.requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
                        // 그 외의 모든 요청은 반드시 인증(로그인)된 사용자만 접근 가능
                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth2 -> oauth2
                        // 소셜 로그인 성공 후 처리를 담당할 핸들러와 서비스를 지정합니다.
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                                 UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
