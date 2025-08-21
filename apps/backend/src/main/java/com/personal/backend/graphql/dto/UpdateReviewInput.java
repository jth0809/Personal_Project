package com.personal.backend.graphql.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateReviewInput(
        @NotNull @Min(1) @Max(5) int rating,
        @NotBlank String comment
) {}
