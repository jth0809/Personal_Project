package com.personal.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class OrderDto {

    /**
     * 클라이언트가 '주문 생성'을 요청할 때 사용하는 DTO
     * 장바구니에 담긴 상품 ID와 수량 목록을 받습니다.
     */
    public record CreateRequest(
        @NotEmpty(message = "주문할 상품이 없습니다.")
        @Valid
        List<OrderItemRequest> orderItems
    ) {}

    public record OrderItemRequest(
        @NotNull(message = "상품 ID는 필수입니다.")
        Long productId,
        @Min(value = 1, message = "주문 수량은 1개 이상이어야 합니다.")
        int count
    ) {}


    /**
     * 클라이언트에게 '주문 내역'을 응답으로 보낼 때 사용하는 DTO
     */
    public record HistoryResponse(
            Long orderId,
            LocalDateTime orderDate,
            String orderStatus,
            List<OrderItemResponse> orderItems
    ) {}

    public record OrderItemResponse(
            String productName,
            int count,
            int orderPrice
    ) {}
}
