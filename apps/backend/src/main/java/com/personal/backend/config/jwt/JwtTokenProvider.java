package com.personal.backend.config.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey; // 👇 java.security.Key 대신 구체적인 SecretKey를 사용합니다.
import java.util.Base64;
import java.util.Date;

@Component // 스프링 빈으로 등록하여 다른 곳에서 주입받아 사용할 수 있도록 함
public class JwtTokenProvider {

    // 👇 핵심 수정: Key 타입을 더 구체적인 SecretKey로 변경합니다.
    private final SecretKey key;
    
    private static final long TOKEN_VALID_TIME = 30 * 60 * 1000L; // 토큰 유효시간 30분

    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(UserDetailsService userDetailsService, JwtProperties jwtProperties) {
        this.userDetailsService = userDetailsService;
        // 주입받은 설정 객체에서 secret 값을 가져옵니다.
        String secretKey = jwtProperties.secret();
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // JWT 토큰 생성 (최신 방식으로 수정)
    public String createToken(String userPk) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userPk) // 'subject'를 설정합니다 (setSubject의 최신 방식)
                .issuedAt(now)   // 토큰 발행 시간 정보 (setIssuedAt의 최신 방식)
                .expiration(new Date(now.getTime() + TOKEN_VALID_TIME)) // 만료 시간 설정 (setExpiration의 최신 방식)
                .signWith(key)   // 사용할 암호화 알고리즘과 비밀키 (알고리즘은 Key에 따라 자동 선택됨)
                .compact();
    }

    // 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 구별 정보 추출 (최신 방식으로 수정)
    public String getUserPk(String token) {
        return Jwts.parser()
                   .verifyWith(this.key) // 👇 이제 key 타입이 SecretKey와 일치합니다.
                   .build()
                   .parseSignedClaims(token)
                   .getPayload()
                   .getSubject();
    }

    // 토큰의 유효성 + 만료일자 확인 (최신 방식으로 수정)
    public boolean validateToken(String jwtToken) {
        try {
            Jwts.parser().verifyWith(this.key).build().parseSignedClaims(jwtToken);
            return true;
        } catch (Exception e) {
            // 토큰 파싱 중 발생하는 모든 예외를 처리합니다.
            return false;
        }
    }
}