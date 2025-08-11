package com.personal.backend.service;

import com.personal.backend.dto.PaymentDto;
import com.personal.backend.payment.PaymentGateway;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderService orderService;
    private final PaymentGateway paymentGateway; // 👇 TossPaymentGateway가 아닌 PaymentGateway 인터페이스에 의존

    @Transactional
    public Mono<PaymentDto.ConfirmationResponse> confirmPayment(PaymentDto.VerificationRequest request) {
        int originalAmount = orderService.getOrderAmountByPgOrderId(request.orderId());

        if (request.amount() != originalAmount) {
            return Mono.error(new IllegalArgumentException("주문 금액이 일치하지 않습니다."));
        }

        // paymentGateway의 confirm 메소드를 호출 (이것이 Toss인지 PortOne인지 서비스는 알 필요 없음)
        return paymentGateway.confirm(request)
                .doOnSuccess(response -> {
                    orderService.markOrderAsPaid(request.orderId(), request.paymentKey());
                    orderService.processPostPayment(request.orderId());
                });
    }
}