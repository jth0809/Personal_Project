package com.personal.backend.graphql.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateProductInput(
        @NotBlank String name,
        String description,
        @Min(1) int price,
        @NotEmpty(message = "이미지 URL 목록은 비어 있을 수 없습니다.") List<String> imageUrl,
        @NotNull Long categoryId,
        @Min(0) int stockQuantity,
        @Min(0) double discountRate,
        String detailContent
) {}
