package com.personal.backend.graphql.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * GraphQL OrderItemInput 타입에 매핑되는 레코드입니다.
 */
public record OrderItemInput(
        @NotNull
        Long productId,

        @Min(1)
        int count
) {}
