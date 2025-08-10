package com.personal.backend.dto;

import java.util.List;

import com.personal.backend.domain.Product;
import com.personal.backend.domain.User;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public class ProductDto {

    /**
     * 클라이언트가 '상품 생성'을 요청할 때 사용하는 DTO
     */
    public record CreateRequest(
        @NotBlank(message = "상품 이름은 필수 입력 항목입니다.")  
        String name,
        String description,
        @Min(value = 1, message = "가격은 0보다 커야 합니다.")    
        int price,
        @NotNull(message = "이미지 URL 목록은 필수입니다.")
        List<String> imageUrl,
        @NotNull(message = "카테고리 ID는 필수입니다.")
        Long categoryId // 상품이 속할 카테고리의 ID
    ) {
        // 이 DTO를 실제 데이터베이스에 저장될 Product 엔티티로 변환하는 메소드
        // Category는 Service 계층에서 ID를 이용해 조회 후 주입합니다.
        public Product toEntity(User user) {
            return Product.builder()
                    .name(name)
                    .description(description)
                    .price(price)
                    .imageUrl(imageUrl)
                    .user(user)
                    .build();
        }
    }

    /**
     * 클라이언트가 '상품 정보 수정'을 요청할 때 사용하는 DTO
     */
    public record UpdateRequest(
        @NotBlank(message = "상품 이름은 필수입니다.")    
        String name,
        String description,
        @Min(value = 1, message = "가격은 0보다 커야 합니다.")    
        int price,
        @NotNull(message = "이미지 URL 목록은 필수입니다.")
        List<String> imageUrl,
        @NotNull(message = "카테고리 ID는 필수입니다.")
        Long categoryId
    ) {}

    /**
     * 클라이언트에게 '상품 목록'이나 '단일 상품 정보'를 응답으로 보낼 때 사용하는 DTO
     */
    public record Response(
            Long id,
            String name,
            String description,
            int price,
            List<String> imageUrl,
            String categoryName // 카테고리 이름도 함께 전달
    ) {
        // Product 엔티티 객체를 클라이언트에게 보여줄 Response DTO로 변환하는 정적 메소드
        public static Response fromEntity(Product product) {
            return new Response(
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getImageUrl(),
                    product.getCategory() != null ? product.getCategory().getName() : null
            );
        }
    }
}