package com.personal.backend.service;

import com.personal.backend.domain.Category;
import com.personal.backend.domain.Product;
import com.personal.backend.dto.ProductDto;
import com.personal.backend.repository.CategoryRepository;
import com.personal.backend.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("상품 생성 서비스 테스트")
    void createProductTest() {
        // given: 이런 데이터가 주어졌을 때
        ProductDto.CreateRequest requestDto = new ProductDto.CreateRequest("테스트 상품", "설명", 1000, "url", 1L);
        Product product = requestDto.toEntity();
        
        // 👇 핵심 수정: 가짜 CategoryRepository가 어떻게 동작할지 미리 정의합니다.
        // categoryRepository.findById(1L)가 호출되면, 가짜 Category 객체를 담은 Optional을 반환하도록 설정합니다.
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category("테스트 카테고리")));
        
        // productRepository.save가 호출되면, 인자로 받은 product를 그대로 반환하도록 설정합니다.
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // when: createProduct 메소드를 실행하면
        productService.createProduct(requestDto);

        // then: productRepository의 save 메소드가 정확히 1번 호출되었는지를 검증합니다.
        verify(productRepository, times(1)).save(any(Product.class));
    }
}