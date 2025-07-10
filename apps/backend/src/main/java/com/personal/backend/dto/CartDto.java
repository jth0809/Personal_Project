package com.personal.backend.dto;

import java.util.List;

public class CartDto {

    /**
     * 클라이언트가 '장바구니에 상품 추가'를 요청할 때 사용하는 DTO
     */
    public record AddItemRequest(
            Long productId,
            int quantity
    ) {}

    /**
     * 클라이언트에게 '장바구니 전체 내용'을 응답으로 보낼 때 사용하는 DTO
     */
    public record CartResponse(
            List<CartItemResponse> items,
            int totalPrice
    ) {}

    /**
     * 장바구니에 담긴 개별 상품의 정보를 나타내는 DTO
     */
    public record CartItemResponse(
            Long cartItemId, // 장바구니 아이템 자체의 ID (삭제/수정 시 사용)
            Long productId,
            String productName,
            int price,
            String imageUrl,
            int quantity
    ) {}
}