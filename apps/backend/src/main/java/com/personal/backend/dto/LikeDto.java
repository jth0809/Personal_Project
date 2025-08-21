package com.personal.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class LikeDto {
    public record LikeRequest(
        @NotNull(message = "상품 ID는 필수입니다.") 
        @Min(value = 1, message = "상품 ID는 1 이상이어야 합니다.") 
        Long productId
    ) 
    {}
}
