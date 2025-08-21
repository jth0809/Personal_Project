package com.personal.backend.controller;

import com.personal.backend.dto.ProductOptionDto;
import com.personal.backend.service.ProductOptionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "상품 옵션 API", description = "상품 옵션 생성, 삭제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/products/{productId}/options")
public class ProductOptionController {

    private final ProductOptionService productOptionService;

    @Operation(summary = "상품 옵션 생성", description = "특정 상품에 대한 옵션을 생성합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<ProductOptionDto.Response> createOption(@PathVariable Long productId, @RequestBody ProductOptionDto.CreateRequest request) {
        return ResponseEntity.ok(productOptionService.createOption(productId, request));
    }

    @Operation(summary = "상품 옵션 삭제", description = "특정 상품 옵션을 삭제합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{optionId}")
    public ResponseEntity<Void> deleteOption(@PathVariable Long productId, @PathVariable Long optionId) {
        productOptionService.deleteOption(optionId);
        return ResponseEntity.noContent().build();
    }
}
