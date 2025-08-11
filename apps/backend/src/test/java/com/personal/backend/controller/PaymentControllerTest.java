package com.personal.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.backend.dto.PaymentDto;
import com.personal.backend.service.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // 테스트 프로파일 활성화
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    @WithMockUser // 인증된 사용자로 요청을 시뮬레이션
    @DisplayName("결제 검증 API 호출 성공")
    void confirmPayment_Success() throws Exception {
        // given
        PaymentDto.VerificationRequest request = new PaymentDto.VerificationRequest("toss", "test_pk", "test_oid", 10000);
        PaymentDto.ConfirmationResponse serviceResponse = new PaymentDto.ConfirmationResponse("DONE", "test_oid", 10000);

        // Mock 설정: PaymentService가 성공적으로 응답했다고 가정
        when(paymentService.confirmPayment(any(PaymentDto.VerificationRequest.class)))
                .thenReturn(Mono.just(serviceResponse));

        // when & then
        mockMvc.perform(post("/payments/confirm-toss")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.totalAmount").value(10000));
    }
}