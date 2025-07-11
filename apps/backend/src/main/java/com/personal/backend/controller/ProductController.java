package com.personal.backend.controller;

import com.personal.backend.dto.ProductDto;
import com.personal.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // 이 클래스가 REST API를 위한 컨트롤러임을 선언합니다.
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어줍니다.
@RequestMapping("/api/products") // 이 컨트롤러의 모든 API는 "/api/products" 경로로 시작합니다.
public class ProductController {

    private final ProductService productService; // Service 계층에 의존

    /**
     * 새로운 상품을 생성하는 API
     * POST /api/products
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createProduct(@RequestBody ProductDto.CreateRequest request) {
        // 요청 본문(JSON)을 DTO로 받아, 실제 업무는 서비스 계층에 위임합니다.
        productService.createProduct(request);
        return ResponseEntity.ok().build(); // 성공적으로 생성되었음을 알리는 200 OK 응답을 반환합니다.
    }
    /**
     * 특정 상품의 정보를 수정하는 API
     * PUT /api/products/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // 오직 ADMIN 역할만 실행 가능
    public ResponseEntity<ProductDto.Response> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDto.UpdateRequest request) {
        // SonarQube 경고에 따라 불필요한 지역 변수 할당을 제거하고 바로 반환합니다.
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    /**
     * 특정 상품을 삭제하는 API
     * DELETE /api/products/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // 오직 ADMIN 역할만 실행 가능
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build(); // 내용 없이 성공(204 No Content) 응답
    }

    /**
     * 모든 상품 목록을 조회하는 API
     * GET /api/products
     */
    @GetMapping
    public ResponseEntity<List<ProductDto.Response>> getAllProducts() {
        List<ProductDto.Response> products = productService.findProducts(null);
        return ResponseEntity.ok(products); // 조회된 상품 목록과 함께 200 OK 응답을 반환합니다.
    }

    /**
     * 특정 ID의 상품을 조회하는 API
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto.Response> getProductById(@PathVariable Long id) {
        // URL 경로의 {id} 값을 Long 타입의 id 변수로 받습니다.
        ProductDto.Response product = productService.findProductById(id);
        return ResponseEntity.ok(product); // 조회된 상품 정보와 함께 200 OK 응답을 반환합니다.
    }
}
