package com.personal.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class OrderDto {

    @Schema(name = "OrderCreateRequest", description = "주문 생성 요청")
    public record CreateRequest(
        @NotEmpty(message = "주문할 상품이 없습니다.")
        @Valid
        List<OrderItemRequest> orderItems
    ) {}
    
    @Schema(name = "OrderItemRequest", description = "주문 항목 요청")
    public record OrderItemRequest(
        @NotNull(message = "상품 ID는 필수입니다.")
        Long productId,
        @Min(value = 1, message = "주문 수량은 1개 이상이어야 합니다.")
        int count
    ) {}

    @Schema(name = "OrderCancelRequest", description = "주문 취소 요청")
    public record CancelRequest(
        @NotBlank(message = "취소 사유는 필수입니다.")
        String reason
    ) {}

    @Schema(name = "OrderResponse", description = "주문 응답")
    public record CreateResponse(
            String pgOrderId,      // PG사에 전달할 고유 주문 ID
            String orderName,      // 주문명 (예: "고성능 노트북 외 1건")
            Integer amount,        // 최종 결제 금액
            String customerEmail,
            String customerName
    ) {}

    @Schema(name = "OrderHistoryResponse", description = "주문 내역 응답")
    public record HistoryResponse(
            Long id,
            LocalDateTime orderDate,
            String orderStatus,
            List<OrderItemResponse> orderItems
    ) {}

    @Schema(name = "OrderItemResponse", description = "주문 항목 응답")
    public record OrderItemResponse(
            String productName,
            int count,
            int orderPrice
    ) {}
}
