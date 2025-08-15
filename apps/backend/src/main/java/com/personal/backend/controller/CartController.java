package com.personal.backend.controller;

import com.personal.backend.dto.CartDto;
import com.personal.backend.service.CartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "장바구니 API", description = "장바구니 조회, 추가, 삭제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    /**
     * 현재 사용자의 장바구니 내용을 조회하는 API
     * GET /api/cart
     */
    @Operation(summary = "장바구니 조회", description = "장바구니 조회 API")
    @GetMapping
    public ResponseEntity<CartDto.CartResponse> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        CartDto.CartResponse cart = cartService.getCartForUser(userEmail);
        return ResponseEntity.ok(cart);
    }

    /**
     * 장바구니에 상품을 추가하는 API
     * POST /api/cart/items
     */
    @Operation(summary = "장바구니 상품 추가", description = "장바구니 상품 추가 API")
    @PostMapping("/items")
    public ResponseEntity<CartDto.CartResponse> addItemToCart(
            @Valid @RequestBody CartDto.AddItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        CartDto.CartResponse updatedCart = cartService.addItem(userEmail, request);
        return ResponseEntity.ok(updatedCart);
    }

    /**
     * 장바구니의 특정 아이템을 삭제하는 API
     * DELETE /api/cart/items/{itemId}
     */
    @Operation(summary = "장바구니 물건 삭제", description = "장바구니 물건 삭제 API")
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartDto.CartResponse> removeItemFromCart(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        CartDto.CartResponse updatedCart = cartService.removeItemFromCart(userEmail, itemId);
        return ResponseEntity.ok(updatedCart);
    }
}