package com.personal.backend.dto;

import com.personal.backend.domain.ProductOption;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class ProductOptionDto {

    @Schema(name = "ProductOptionCreateRequest", description = "상품 옵션 생성 요청 DTO")
    public record CreateRequest(
        @NotBlank(message = "옵션 그룹 이름은 필수입니다.")
        String optionGroupName,
        @NotBlank(message = "옵션 이름은 필수입니다.")
        String optionName,
        @Min(value = 0, message = "추가 가격은 0 이상이어야 합니다.")
        int additionalPrice,
        @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다.")
        int stockQuantity
    ) {}

    @Schema(name = "ProductOptionResponse", description = "상품 옵션 응답 DTO")
    public record Response(
        Long id,
        String optionGroupName,
        String optionName,
        int additionalPrice,
        int stockQuantity
    ) {
        public static Response fromEntity(ProductOption option) {
            return new Response(
                option.getId(),
                option.getOptionGroupName(),
                option.getOptionName(),
                option.getAdditionalPrice(),
                option.getStockQuantity()
            );
        }
    }
}
