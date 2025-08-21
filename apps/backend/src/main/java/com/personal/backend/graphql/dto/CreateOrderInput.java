package com.personal.backend.graphql.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderInput(
        @NotEmpty @Valid List<OrderItemInput> items
) {}
