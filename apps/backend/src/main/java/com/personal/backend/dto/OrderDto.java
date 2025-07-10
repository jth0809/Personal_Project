package com.personal.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    /**
     * 클라이언트가 '주문 생성'을 요청할 때 사용하는 DTO
     * 장바구니에 담긴 상품 ID와 수량 목록을 받습니다.
     */
    public record CreateRequest(
        List<OrderItemRequest> orderItems
    ) {}

    public record OrderItemRequest(
        Long productId,
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
