package com.personal.backend.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.personal.backend.domain.Product;
import com.personal.backend.domain.ProductOption;
import com.personal.backend.domain.Tag;
import com.personal.backend.domain.User;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import io.swagger.v3.oas.annotations.media.Schema;

public class ProductDto {

    /**
     * 클라이언트가 '상품 생성'을 요청할 때 사용하는 DTO
     */
    @Schema(name = "ProductCreateRequest", description = "상품 생성 요청 DTO")
    public record CreateRequest(
        @NotBlank(message = "상품 이름은 필수 입력 항목입니다.")  
        String name,
        String description,
        @Min(value = 1, message = "가격은 0보다 커야 합니다.")    
        int price,
        @NotEmpty(message = "이미지 URL 목록은 비어 있을 수 없습니다.")
        List<String> imageUrl,
        @NotNull(message = "카테고리 ID는 필수입니다.")
        Long categoryId, // 상품이 속할 카테고리의 ID
        @Min(value = 0, message = "재고는 0 이상이어야 합니다.")
        int stockQuantity,
        @Min(value = 0, message = "할인율은 0 이상이어야 합니다.")
        double discountRate
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
                    .stockQuantity(stockQuantity)
                    .discountRate(discountRate)
                    .build();
        }
    }

    /**
     * 클라이언트가 '상품 정보 수정'을 요청할 때 사용하는 DTO
     */
    @Schema(name = "ProductUpdateRequest", description = "상품 수정 요청 DTO")
    public record UpdateRequest(
        @NotBlank(message = "상품 이름은 필수입니다.")    
        String name,
        String description,
        @Min(value = 1, message = "가격은 0보다 커야 합니다.")    
        int price,
        @NotEmpty(message = "이미지 URL 목록은 비어 있을 수 없습니다.")
        List<String> imageUrl,
        @NotNull(message = "카테고리 ID는 필수입니다.")
        Long categoryId,
        @Min(value = 0, message = "재고는 0 이상이어야 합니다.")
        int stockQuantity,
        String detailContent,
        @Min(value = 0, message = "할인율은 0 이상이어야 합니다.")
        double discountRate
    ) {}

    /**
     * 클라이언트에게 '상품 목록'이나 '단일 상품 정보'를 응답으로 보낼 때 사용하는 DTO
     */
    @Schema(name = "ProductResponse", description = "상품 조회 응답 DTO")
    public record Response(
        Long id,
        String name,
        String description,
        int originalPrice,
        int discountedPrice,
        int stockQuantity,
        List<String> imageUrl,
        String categoryName,
        String detailContent,
        int likeCount,
        int reviewCount,
        double averageRating,
        double discountRate,
        boolean isLiked,
        List<ProductOptionDto.Response> options,
        List<TagDto.Response> tags
    ) {
        // 정적 팩토리 메소드는 그대로 유지하여 변환 로직을 캡슐화합니다.
        public static Response from(Product product, boolean isLiked, String detailContent, List<ProductOption> options, List<Tag> tags) {
            List<ProductOptionDto.Response> optionResponses = options.stream()
                    .map(ProductOptionDto.Response::fromEntity)
                    .toList();
            List<TagDto.Response> tagResponses = tags.stream()
                    .map(TagDto.Response::fromEntity)
                    .toList();

            return new Response(
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    product.getOriginalPrice(),
                    product.getDiscountedPrice(),
                    product.getStockQuantity(),
                    product.getImageUrl(),
                    product.getCategory() != null ? product.getCategory().getName() : null,
                    detailContent,
                    product.getLikeCount(),
                    product.getReviewCount(),
                    product.getAverageRating(),
                    product.getDiscountRate(),
                    isLiked,
                    optionResponses,
                    tagResponses
            );
        }

        public static Response fromEntity(Product product) {
            return from(product, false, null, product.getOptions(), product.getProductTags().stream().map(pt -> pt.getTag()).toList());
        }
    }
}