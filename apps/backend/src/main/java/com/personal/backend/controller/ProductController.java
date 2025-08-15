package com.personal.backend.controller;

import com.personal.backend.dto.ProductDto;
import com.personal.backend.dto.ShippingInfoDto;
import com.personal.backend.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "상품 API", description = "상품 생성, 수정, 삭제, 조회, 상세 조회 API")
@RestController // 이 클래스가 REST API를 위한 컨트롤러임을 선언합니다.
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다.
@RequestMapping("/products") // 이 컨트롤러의 모든 API는 "/api/products" 경로로 시작합니다.
@Validated
public class ProductController {

    private final ProductService productService; // Service 계층에 의존

    /**
     * 새로운 상품을 생성하는 API
     * POST /api/products
     */
    @Operation(summary = "상품 생성", description = "상품 생성 API")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createProduct(
        @Valid @RequestBody ProductDto.CreateRequest request,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userEmail = userDetails.getUsername();
        productService.createProduct(request,userEmail);
        return ResponseEntity.ok().build(); // 성공적으로 생성되었음을 알리는 200 OK 응답을 반환합니다.
    }
    /**
     * 특정 상품의 정보를 수정하는 API
     * PUT /api/products/{id}
     */
    @Operation(summary = "상품 수정", description = "상품 수정 API")
    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')") // 오직 ADMIN 역할만 실행 가능
    public ResponseEntity<ProductDto.Response> updateProduct(
            @Min(value = 0, message = "유효하지 않은 상품 ID입니다.") @PathVariable Long productId,
            @Valid @RequestBody ProductDto.UpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) 
    {
        String userEmail = userDetails.getUsername();
        return ResponseEntity.ok(productService.updateProduct(productId, request, userEmail));
    }

    /**
     * 특정 상품을 삭제하는 API
     * DELETE /api/products/{id}
     */
    @Operation(summary = "상품 삭제", description = "상품 삭제 API")
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')") // 오직 ADMIN 역할만 실행 가능
    public ResponseEntity<Void> deleteProduct(
        @Min(value = 0, message = "유효하지 않은 상품 ID입니다.") @PathVariable Long productId,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userEmail = userDetails.getUsername();
        productService.deleteProduct(productId,userEmail);
        return ResponseEntity.noContent().build(); // 내용 없이 성공(204 No Content) 응답
    }

    /**
     * 모든 상품 목록을 조회하는 API
     * GET /api/products
     */
    @Operation(summary = "상품 목록 조회", description = "상품 목록 조회 API Keyword, categoryId로 검색과 카테고리별 필터링 가능")
    @GetMapping
    public ResponseEntity<Page<ProductDto.Response>> getAllProducts(
        @RequestParam(required = false) String keyword,
        @Min(value = 0, message = "유효하지 않은 카테고리 ID입니다.") @RequestParam(required = false) Long categoryId,
        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
        )
         {
        Page<ProductDto.Response> productsPage = productService.findProducts(keyword, categoryId, pageable);
        return ResponseEntity.ok(productsPage); // 조회된 상품 목록과 함께 200 OK 응답을 반환합니다.
    }

    /**
     * 특정 ID의 상품을 조회하는 API
     * GET /api/products/{id}
     */
    @Operation(summary = "상품 상세 조회", description = "상품 상세 조회 API")
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto.Response> getProductById(@Min(value = 0, message = "유효하지 않은 상품 ID입니다.") @PathVariable Long productId) {
        // URL 경로의 {id} 값을 Long 타입의 id 변수로 받습니다.
        ProductDto.Response product = productService.findProductById(productId);
        return ResponseEntity.ok(product); // 조회된 상품 정보와 함께 200 OK 응답을 반환합니다.
    }

    @Operation(summary = "상품 배송 정책 조회", description = "특정 상품의 배송 정책 정보를 조회합니다.")
    @GetMapping("/{productId}/shipping")
    public ResponseEntity<ShippingInfoDto.Response> getProductShippingInfo(@Min(value = 0, message = "유효하지 않은 상품 ID입니다.") @PathVariable Long productId) {
        ShippingInfoDto.Response shippingInfo = productService.getShippingInfoByProductId(productId);
        return ResponseEntity.ok(shippingInfo);
    }
}
