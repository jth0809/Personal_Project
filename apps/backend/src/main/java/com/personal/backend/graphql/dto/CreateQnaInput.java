package com.personal.backend.graphql.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateQnaInput(
        @NotNull Long productId,
        @NotBlank String question
) {}
