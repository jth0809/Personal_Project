package com.personal.backend.controller;

import com.personal.backend.dto.OrderDto;
import com.personal.backend.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Tag(name = "주문 API", description = "주문 생성, 조회, 상세조회, 취소 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Validated
public class OrderController {

    private final OrderService orderService;

    /**
     * 새로운 주문을 생성하는 API
     * POST /api/orders
     */
    @Operation(summary = "주문 생성", description = "주문 생성 API")
    @PostMapping
    public ResponseEntity<OrderDto.CreateResponse> createOrder(
        @Valid @RequestBody OrderDto.CreateRequest request,
        @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        OrderDto.CreateResponse newOrder = orderService.createOrder(userEmail, request);
        return ResponseEntity.ok(newOrder);
    }

    /**
     * 현재 사용자의 주문 내역을 조회하는 API
     * GET /api/orders/history
     */
    @Operation(summary = "모든 주문 조회", description = "모든 주문 조회 API")
    @GetMapping("/history")
    public ResponseEntity<Page<OrderDto.HistoryResponse>> getOrderHistory(
        @AuthenticationPrincipal UserDetails userDetails,
        @PageableDefault(size = 10, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable
        ) {
        String userEmail = userDetails.getUsername();
        Page<OrderDto.HistoryResponse> history = orderService.getOrderHistory(userEmail, pageable);
        return ResponseEntity.ok(history);
    }

    /**
     * 특정 주문을 상세 조회하는 API
     * GET /api/orders/{orderId}
     */
    @Operation(summary = "주문 상세 조회", description = "주문 상세 조회 API")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto.HistoryResponse> getOrderDetails(
            @Min(value = 0, message = "유효하지 않은 주문 ID입니다.") @PathVariable Long orderId,
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
            @Min(value = 0, message = "유효하지 않은 주문 ID입니다.") @PathVariable Long orderId,
            @NotNull @RequestBody OrderDto.CancelRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        // 서비스로부터 업데이트된 주문 정보를 받아 반환합니다.
        OrderDto.HistoryResponse canceledOrder = orderService.cancelOrder(userEmail, orderId, request.reason());
        return ResponseEntity.ok(canceledOrder);
    }
}
