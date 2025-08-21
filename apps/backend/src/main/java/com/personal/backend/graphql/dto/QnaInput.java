package com.personal.backend.graphql.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QnaInput(
        @NotNull Long productId,
        @NotBlank String question
) {}
