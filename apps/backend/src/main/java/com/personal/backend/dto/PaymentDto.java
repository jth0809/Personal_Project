package com.personal.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PaymentDto {
    
    // 프론트엔드에서 결제 검증을 위해 보낼 정보
    public record VerificationRequest(
            @NotBlank(message = "결제사 정보는 필수입니다.")
            String provider,

            @NotBlank(message = "결제 키(paymentKey)는 필수입니다.")
            String paymentKey,

            @NotBlank(message = "주문 ID(orderId)는 필수입니다.")
            String orderId,

            @NotNull(message = "금액은 필수입니다.")
            @Positive(message = "금액은 0보다 커야 합니다.")
            Integer amount
    ) {}

    // 토스페이먼츠로부터 받은 최종 승인 응답 정보 (필요한 필드만 일부 정의)
    public record ConfirmationResponse(
            String status,
            String orderId,
            Integer totalAmount
    ) {}
}