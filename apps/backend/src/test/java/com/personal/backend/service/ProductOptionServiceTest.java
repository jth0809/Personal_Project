package com.personal.backend.service;

import com.personal.backend.domain.Product;
import com.personal.backend.domain.ProductOption;
import com.personal.backend.dto.ProductOptionDto;
import com.personal.backend.repository.ProductOptionRepository;
import com.personal.backend.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductOptionServiceTest {

    @InjectMocks
    private ProductOptionService productOptionService;

    @Mock
    private ProductOptionRepository productOptionRepository;

    @Mock
    private ProductRepository productRepository;

    private Product dummyProduct;

    @BeforeEach
    void setUp() {
        dummyProduct = Product.builder().name("Test Product").build();
        try {
            var idField = Product.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(dummyProduct, 1L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("상품 옵션 생성 성공")
    void createOption_Success() {
        // given
        Long productId = 1L;
        ProductOptionDto.CreateRequest request = new ProductOptionDto.CreateRequest("색상", "빨강", 1000, 10);
        ProductOption savedOption = ProductOption.builder()
                .product(dummyProduct)
                .optionGroupName(request.optionGroupName())
                .optionName(request.optionName())
                .additionalPrice(request.additionalPrice())
                .stockQuantity(request.stockQuantity())
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(dummyProduct));
        when(productOptionRepository.save(any(ProductOption.class))).thenReturn(savedOption);

        // when
        ProductOptionDto.Response response = productOptionService.createOption(productId, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.optionName()).isEqualTo("빨강");
        assertThat(response.additionalPrice()).isEqualTo(1000);
        verify(productRepository, times(1)).findById(productId);
        verify(productOptionRepository, times(1)).save(any(ProductOption.class));
    }

    @Test
    @DisplayName("상품 옵션 생성 실패 - 상품 없음")
    void createOption_Fail_ProductNotFound() {
        // given
        Long productId = 99L; // Non-existent product
        ProductOptionDto.CreateRequest request = new ProductOptionDto.CreateRequest("색상", "빨강", 1000, 10);

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> productOptionService.createOption(productId, request));
        verify(productOptionRepository, never()).save(any(ProductOption.class));
    }

    @Test
    @DisplayName("상품 옵션 삭제 성공")
    void deleteOption_Success() {
        // given
        Long optionId = 1L;
        doNothing().when(productOptionRepository).deleteById(optionId);

        // when
        productOptionService.deleteOption(optionId);

        // then
        verify(productOptionRepository, times(1)).deleteById(optionId);
    }
}
