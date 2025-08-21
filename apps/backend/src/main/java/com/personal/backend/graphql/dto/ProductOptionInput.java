package com.personal.backend.graphql.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * GraphQL ProductOptionInput 타입에 매핑되는 레코드입니다.
 */
public record ProductOptionInput(
        @NotBlank(message = "옵션 그룹 이름은 필수입니다.")
        String optionGroupName,

        @NotBlank(message = "옵션 이름은 필수입니다.")
        String optionName,

        @Min(value = 0, message = "추가 가격은 0 이상이어야 합니다.")
        int additionalPrice,

        @Min(value = 0, message = "재고는 0 이상이어야 합니다.")
        int stockQuantity
) {}
