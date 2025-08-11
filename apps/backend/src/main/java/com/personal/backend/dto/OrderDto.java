package com.personal.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class OrderDto {
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

    public record CancelRequest(
        @NotBlank(message = "취소 사유는 필수입니다.")
        String reason
    ) {}

    public record CreateResponse(
            String pgOrderId,      // PG사에 전달할 고유 주문 ID
            String orderName,      // 주문명 (예: "고성능 노트북 외 1건")
            Integer amount,        // 최종 결제 금액
            String customerEmail,
            String customerName
    ) {}
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
