package com.personal.backend.graphql;

import com.personal.backend.dto.ProductOptionDto;
import com.personal.backend.graphql.dto.ProductOptionInput;
import com.personal.backend.service.ProductOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ProductOptionGraphqlController {

    private final ProductOptionService productOptionService;

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductOptionDto.Response createProductOption(@Argument Long productId, @Argument("input") ProductOptionInput input) {
        ProductOptionDto.CreateRequest request = new ProductOptionDto.CreateRequest(
                input.optionGroupName(),
                input.optionName(),
                input.additionalPrice(),
                input.stockQuantity()
        );
        return productOptionService.createOption(productId, request);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteProductOption(@Argument Long optionId) {
        productOptionService.deleteOption(optionId);
        return true;
    }
}