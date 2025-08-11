package com.personal.backend.config.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * application.properties의 'payment'로 시작하는 설정값들을 담는 클래스
 */
@ConfigurationProperties(prefix = "payment")
public record PaymentProperties(
    // payment.toss 하위 프로퍼티들을 매핑할 Toss 객체
    Toss toss
) {
    /**
     * Toss Payments 관련 설정값
     * @param secretKey 서버 사이드에서 결제 승인 시 사용할 테스트 또는 운영 시크릿 키
     */
    public record Toss(String secretKey) {}
}