package com.personal.backend.service;

import com.personal.backend.dto.PaymentDto;
import com.personal.backend.payment.PaymentGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private OrderService orderService;

    @Mock
    private PaymentGateway paymentGateway;

    @Test
    @DisplayName("결제 승인 성공 시 주문 상태 변경 및 후속 처리 로직 호출 검증")
    void confirmPayment_Success() {
        // given
        PaymentDto.VerificationRequest request = new PaymentDto.VerificationRequest("toss", "test_pk", "test_oid", 10000);
        PaymentDto.ConfirmationResponse pgResponse = new PaymentDto.ConfirmationResponse("DONE", "test_oid", 10000);

        // Mock 설정: DB의 주문 금액과 요청 금액이 일치한다고 가정
        when(orderService.getOrderAmountByPgOrderId(request.orderId())).thenReturn(10000);
        // Mock 설정: PaymentGateway가 성공적으로 결제를 승인했다고 가정
        when(paymentGateway.confirm(request)).thenReturn(Mono.just(pgResponse));

        // when
        Mono<PaymentDto.ConfirmationResponse> result = paymentService.confirmPayment(request);

        // then
        // StepVerifier를 사용하여 비동기(Mono) 결과를 검증
        StepVerifier.create(result)
                .expectNext(pgResponse)
                .verifyComplete();
        
        // [핵심] 결제 성공 후, OrderService의 메소드들이 정확히 1번씩 호출되었는지 검증
        verify(orderService, times(1)).markOrderAsPaid(request.orderId(), request.paymentKey());
        verify(orderService, times(1)).processPostPayment(request.orderId());
    }

    @Test
    @DisplayName("결제 승인 실패 - 주문 금액 불일치")
    void confirmPayment_Fail_AmountMismatch() {
        // given
        PaymentDto.VerificationRequest request = new PaymentDto.VerificationRequest("toss", "test_pk", "test_oid", 10000);

        // Mock 설정: DB의 주문 금액(12000)과 요청 금액(10000)이 다르다고 가정
        when(orderService.getOrderAmountByPgOrderId(request.orderId())).thenReturn(12000);

        // when
        Mono<PaymentDto.ConfirmationResponse> result = paymentService.confirmPayment(request);

        // then
        // 금액이 일치하지 않아 IllegalArgumentException이 발생하는지 검증
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        // [핵심] 금액이 달랐으므로, PaymentGateway와 후속 처리 로직은 절대 호출되면 안 됨
        verify(paymentGateway, never()).confirm(any());
        verify(orderService, never()).markOrderAsPaid(anyString(), anyString());
        verify(orderService, never()).processPostPayment(anyString());
    }
}