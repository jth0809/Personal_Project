package com.personal.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.backend.dto.OrderDto;
import com.personal.backend.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@WithMockUser(username = "test@user.com") // 모든 테스트에 "test@user.com" 사용자로 로그인한 상태를 적용
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @Test
    @DisplayName("주문 생성 API 성공")
    void createOrder_Success() throws Exception {
        // given
        String userEmail = "test@user.com";
        OrderDto.CreateRequest request = new OrderDto.CreateRequest(
                List.of(new OrderDto.OrderItemRequest(100L, 2))
        );
        Long newOrderId = 1L;

        // Mock a call to the service
        when(orderService.createOrder(eq(userEmail), any(OrderDto.CreateRequest.class))).thenReturn(newOrderId);

        // when & then
        mockMvc.perform(post("/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("주문이 성공적으로 생성되었습니다. 주문 ID: " + newOrderId));
    }

    @Test
    @DisplayName("주문 내역 조회 API 성공 (페이지네이션 적용)")
    void getOrderHistory_Success_WithPagination() throws Exception {
        // given
        String userEmail = "test@user.com";
        // 1. Mock Service가 반환할 Page<HistoryResponse> 객체 생성
        OrderDto.HistoryResponse historyResponse = new OrderDto.HistoryResponse(
                1L, LocalDateTime.now(), "COMPLETED", List.of()
        );
        Page<OrderDto.HistoryResponse> responsePage = new PageImpl<>(List.of(historyResponse));
        
        // 2. Mock 설정
        when(orderService.getOrderHistory(eq(userEmail), any(Pageable.class))).thenReturn(responsePage);

        // when & then
        // 3. MockMvc 요청 시 URL에 페이지 파라미터 추가
        mockMvc.perform(get("/orders/history?page=0&size=10"))
                .andExpect(status().isOk())
                // 4. JSON 응답 구조가 Page 형식에 맞는지 검증
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }
    
    @Test
    @DisplayName("주문 상세 조회 API 성공")
    void getOrderDetails_Success() throws Exception {
        // given
        String userEmail = "test@user.com";
        Long orderId = 1L;
        OrderDto.HistoryResponse orderDetails = new OrderDto.HistoryResponse(
                orderId, LocalDateTime.now(), "PENDING", List.of()
        );
        
        // Mock a call to the service
        when(orderService.findOrderDetails(userEmail, orderId)).thenReturn(orderDetails);
        
        // when & then
        mockMvc.perform(get("/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.orderStatus").value("PENDING"));
    }

    @Test
    @DisplayName("주문 취소 API 성공")
    void cancelOrder_Success() throws Exception {
        // given
        String userEmail = "test@user.com";
        Long orderId = 1L;
        OrderDto.HistoryResponse canceledOrderResponse = new OrderDto.HistoryResponse(
                orderId, LocalDateTime.now(), "CANCELED", List.of()
        );
        
        // Mock a call to the service
        when(orderService.cancelOrder(userEmail, orderId)).thenReturn(canceledOrderResponse);
        
        // when & then
        mockMvc.perform(delete("/orders/{orderId}", orderId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("CANCELED"));
    }
}