package com.personal.backend.controller;

import com.personal.backend.dto.PaymentDto;
import com.personal.backend.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "결제 API", description = "외부 결제 결과를 검증")
@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    
    @Operation(summary = "결제 확인 (토스페이먼츠)", description = "결제 확인 API")
    @PostMapping("/confirm-toss")
    public ResponseEntity<PaymentDto.ConfirmationResponse> confirmPayment(
            @Valid @RequestBody PaymentDto.VerificationRequest request
    ) {
        PaymentDto.ConfirmationResponse response = paymentService.confirmPayment(request).block();
        
        return ResponseEntity.ok(response);
    }
}