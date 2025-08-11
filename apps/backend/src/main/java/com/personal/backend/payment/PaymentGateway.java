package com.personal.backend.payment;
import com.personal.backend.dto.PaymentDto;
import reactor.core.publisher.Mono;

public interface PaymentGateway {
    
    // 결제를 검증하고 승인하는 기능
    Mono<PaymentDto.ConfirmationResponse> confirm(PaymentDto.VerificationRequest request);

    // (선택사항) 결제 취소 기능
    Mono<PaymentDto.ConfirmationResponse> cancel(String paymentKey, String cancelReason);
}