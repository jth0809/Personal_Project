package com.personal.backend.controller;

import com.personal.backend.dto.OrderDto;
import com.personal.backend.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "주문 API", description = "주문 생성, 조회, 상세조회, 취소 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * 새로운 주문을 생성하는 API
     * POST /api/orders
     */
    @Operation(summary = "주문 생성", description = "주문 생성 API")
    @PostMapping
    public ResponseEntity<String> createOrder(
        @RequestBody OrderDto.CreateRequest request,
        @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        Long orderId = orderService.createOrder(userEmail, request);
        return ResponseEntity.ok("주문이 성공적으로 생성되었습니다. 주문 ID: " + orderId);
    }

    /**
     * 현재 사용자의 주문 내역을 조회하는 API
     * GET /api/orders/history
     */
    @Operation(summary = "주문 조회", description = "주문 조회 API")
    @GetMapping("/history")
    public ResponseEntity<List<OrderDto.HistoryResponse>> getOrderHistory(
        @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        List<OrderDto.HistoryResponse> history = orderService.getOrderHistory(userEmail);
        return ResponseEntity.ok(history);
    }

    /**
     * 특정 주문을 상세 조회하는 API
     * GET /api/orders/{orderId}
     */
    @Operation(summary = "주문 상세 조회", description = "주문 상세 조회 API")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto.HistoryResponse> getOrderDetails(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        OrderDto.HistoryResponse orderDetails = orderService.findOrderDetails(userEmail, orderId);
        return ResponseEntity.ok(orderDetails);
    }

    /**
     * 특정 주문을 취소하는 API
     * DELETE /api/orders/{orderId}
     */
    @Operation(summary = "주문 취소", description = "주문 취소 API")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<OrderDto.HistoryResponse> cancelOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        // 서비스로부터 업데이트된 주문 정보를 받아 반환합니다.
        OrderDto.HistoryResponse canceledOrder = orderService.cancelOrder(userEmail, orderId);
        return ResponseEntity.ok(canceledOrder);
    }
}
