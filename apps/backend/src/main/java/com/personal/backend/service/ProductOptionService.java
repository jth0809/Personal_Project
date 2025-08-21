package com.personal.backend.service;

import com.personal.backend.domain.Product;
import com.personal.backend.domain.ProductOption;
import com.personal.backend.dto.ProductOptionDto;
import com.personal.backend.repository.ProductOptionRepository;
import com.personal.backend.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductOptionService {

    private final ProductOptionRepository productOptionRepository;
    private final ProductRepository productRepository;

    public ProductOptionDto.Response createOption(Long productId, ProductOptionDto.CreateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        ProductOption option = ProductOption.builder()
                .product(product)
                .optionGroupName(request.optionGroupName())
                .optionName(request.optionName())
                .additionalPrice(request.additionalPrice())
                .stockQuantity(request.stockQuantity())
                .build();

        ProductOption savedOption = productOptionRepository.save(option);
        return ProductOptionDto.Response.fromEntity(savedOption);
    }

    public void deleteOption(Long optionId) {
        productOptionRepository.deleteById(optionId);
    }
}
