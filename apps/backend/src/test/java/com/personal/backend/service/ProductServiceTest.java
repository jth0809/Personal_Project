package com.personal.backend.service;

import com.personal.backend.domain.Category;
import com.personal.backend.domain.Product;
import com.personal.backend.dto.ProductDto;
import com.personal.backend.repository.CategoryRepository;
import com.personal.backend.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private Category dummyCategory;
    private Product dummyProduct;

    @BeforeEach
    void setUp() {
        // 모든 테스트에서 공통적으로 사용할 가짜 카테고리와 상품 객체를 설정합니다.
        dummyCategory = new Category("테스트 카테고리");
        // 실제 엔티티는 ID를 가지고 있으므로, 리플렉션을 사용해 임시로 ID를 설정해줍니다.
        try {
            var idField = Category.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(dummyCategory, 1L);
        } catch (Exception e) { e.printStackTrace(); }

        dummyProduct = Product.builder()
                .name("테스트 상품")
                .price(10000)
                .description("설명입니다")
                .imageUrl("image.jpg")
                .category(dummyCategory)
                .build();
    }

    @Test
    @DisplayName("상품 생성 성공")
    void createProduct_Success() {
        // given
        ProductDto.CreateRequest request = new ProductDto.CreateRequest("새 상품",  "새 설명", 15000,"new.jpg", 1L);

        // Mock 설정: categoryRepository.findById가 호출되면 dummyCategory를 반환
        when(categoryRepository.findById(request.categoryId())).thenReturn(Optional.of(dummyCategory));
        // Mock 설정: productRepository.save가 호출되면 dummyProduct를 반환
        when(productRepository.save(any(Product.class))).thenReturn(dummyProduct);

        // when
        ProductDto.Response response = productService.createProduct(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo(dummyProduct.getName());
        verify(categoryRepository, times(1)).findById(1L); // categoryRepository.findById가 1L 인자와 함께 1번 호출되었는지 검증
        verify(productRepository, times(1)).save(any(Product.class)); // productRepository.save가 1번 호출되었는지 검증
    }

    @Test
    @DisplayName("상품 생성 실패 - 존재하지 않는 카테고리")
    void createProduct_Fail_CategoryNotFound() {
        // given
        ProductDto.CreateRequest request = new ProductDto.CreateRequest("새 상품",  "새 설명", 15000,"new.jpg", 99L);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty()); // 존재하지 않는 카테고리 ID로 조회 시 빈 Optional 반환

        // when & then
        assertThrows(EntityNotFoundException.class, () -> productService.createProduct(request));
        verify(productRepository, never()).save(any(Product.class)); // save 메소드가 호출되지 않았는지 검증
    }

    @Test
    @DisplayName("상품 수정 성공")
    void updateProduct_Success() {
        // given
        Long productId = 1L;
        ProductDto.UpdateRequest request = new ProductDto.UpdateRequest("수정된 이름", "수정된 설명", 20000, "updated.jpg", 1L);
        when(productRepository.findById(productId)).thenReturn(Optional.of(dummyProduct));

        // when
        ProductDto.Response response = productService.updateProduct(productId, request);

        // then
        assertThat(response.name()).isEqualTo("수정된 이름");
        assertThat(response.price()).isEqualTo(20000);
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("상품 삭제 성공")
    void deleteProduct_Success() {
        // given
        Long productId = 1L;
        when(productRepository.existsById(productId)).thenReturn(true);
        doNothing().when(productRepository).deleteById(productId); // deleteById는 반환값이 없으므로 doNothing() 사용

        // when
        productService.deleteProduct(productId);

        // then
        verify(productRepository, times(1)).deleteById(productId);
    }
    
    @Test
    @DisplayName("전체 상품 목록 조회")
    void findProducts_FindAll() {
        // given
        when(productRepository.findAll()).thenReturn(List.of(dummyProduct, dummyProduct));

        // when
        List<ProductDto.Response> products = productService.findProducts(null);

        // then
        assertThat(products).hasSize(2);
        verify(productRepository, times(1)).findAll();
        verify(productRepository, never()).findByCategoryId(anyLong());
    }

    @Test
    @DisplayName("카테고리별 상품 목록 조회")
    void findProducts_FindByCategory() {
        // given
        Long categoryId = 1L;
        when(productRepository.findByCategoryId(categoryId)).thenReturn(List.of(dummyProduct));
        
        // when
        List<ProductDto.Response> products = productService.findProducts(categoryId);

        // then
        assertThat(products).hasSize(1);
        verify(productRepository, never()).findAll();
        verify(productRepository, times(1)).findByCategoryId(categoryId);
    }
}