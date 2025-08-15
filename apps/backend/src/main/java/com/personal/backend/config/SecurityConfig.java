package com.personal.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.personal.backend.config.jwt.JwtAuthenticationFilter;
import com.personal.backend.config.jwt.JwtTokenProvider;
import com.personal.backend.config.oauth.OAuth2AuthenticationSuccessHandler;
import com.personal.backend.service.CustomOAuth2UserService;

import jakarta.servlet.http.HttpServletResponse;

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
                .formLogin(form -> form.disable()) // 폼 기반 로그인을 비활성화합니다. (로그인 페이지 사용 안 함)
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 인증을 비활성화합니다.
                .exceptionHandling(exception -> exception
                        // 인증되지 않은 사용자가 보호된 리소스에 접근할 때의 동작을 정의합니다.
                        .authenticationEntryPoint((request, response, authException) -> {
                            // 리디렉션 대신, 401 Unauthorized 상태 코드와 간단한 메시지를 반환합니다.
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("Unauthorized");
                        })
                )
                // 3. 각 URL 경로별로 접근 권한을 설정합니다.
                .authorizeHttpRequests(authz -> authz
                        // 공개할 경로들을 명시적으로 지정
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products/**", "/categories/**","/qna/**","/reviews/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        
                        // 위에서 지정한 경로 외의 모든 요청은 인증을 요구
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
