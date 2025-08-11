package com.personal.backend.payment;

import com.personal.backend.config.payment.PaymentProperties;
import com.personal.backend.dto.PaymentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Base64;
import java.util.Map;

@Component // 이 클래스를 스프링 Bean으로 등록
@RequiredArgsConstructor
public class TossPaymentGateway implements PaymentGateway {

    private final WebClient webClient;
    private final PaymentProperties paymentProperties;
    private static final String TOSS_CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private static final String TOSS_CANCEL_URL = "https://api.tosspayments.com/v1/payments/";
    @Override
    public Mono<PaymentDto.ConfirmationResponse> confirm(PaymentDto.VerificationRequest request) {
        String secretKey = paymentProperties.toss().secretKey();
        String encodedSecretKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());

        return webClient.post()
                .uri(TOSS_CONFIRM_URL)
                .header("Authorization", "Basic " + encodedSecretKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentDto.ConfirmationResponse.class);
    }

    @Override
    public Mono<PaymentDto.ConfirmationResponse> cancel(String paymentKey, String cancelReason) {
        String secretKey = paymentProperties.toss().secretKey();
        String encodedSecretKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());

        // WebClient를 사용하여 토스페이먼츠의 결제 취소 API를 호출하고,
        // 그 결과를 Mono<ConfirmationResponse> 형태로 즉시 반환합니다.
        return webClient.post()
                .uri(TOSS_CANCEL_URL + paymentKey + "/cancel")
                .header("Authorization", "Basic " + encodedSecretKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("cancelReason", cancelReason))
                .retrieve()
                .bodyToMono(PaymentDto.ConfirmationResponse.class);
    }
}