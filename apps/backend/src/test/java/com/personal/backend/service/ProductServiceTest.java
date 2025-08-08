package com.personal.backend.service;

import com.personal.backend.domain.Category;
import com.personal.backend.domain.Product;
import com.personal.backend.domain.User;
import com.personal.backend.domain.UserRole;
import com.personal.backend.dto.CategoryDto;
import com.personal.backend.dto.ProductDto;
import com.personal.backend.repository.CategoryRepository;
import com.personal.backend.repository.ProductRepository;
import com.personal.backend.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    private Category dummyCategory;
    private Product dummyProduct;
    private User dummyUser;

    @BeforeEach
    void setUp() {
        // 모든 테스트에서 공통적으로 사용할 가짜 카테고리와 상품 객체를 설정합니다.
        dummyCategory = new Category("테스트 카테고리");
        dummyUser = User.builder().email("test@user.com").role(UserRole.USER).build();
        // 실제 엔티티는 ID를 가지고 있으므로, 리플렉션을 사용해 임시로 ID를 설정해줍니다.
        try {
            var idField = Category.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(dummyCategory, 1L);
        } catch (Exception e) { e.printStackTrace(); }
        
        try { // 테스트를 위해 임시 ID 설정
            var idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(dummyUser, 1L);
        } catch (Exception e) { e.printStackTrace(); }

        dummyProduct = Product.builder()
                .name("테스트 상품")
                .price(10000)
                .description("설명입니다")
                .imageUrl(List.of("image.jpg"))
                .category(dummyCategory)
                .user(dummyUser)
                .build();
    }

    @Test
    @DisplayName("상품 생성 성공")
    void createProduct_Success() {
        // given
        String userEmail = "test@user.com";
        ProductDto.CreateRequest request = new ProductDto.CreateRequest("새 상품",  "새 설명", 15000,List.of("new.jpg"), 1L);
        
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(dummyUser));
        // Mock 설정: categoryRepository.findById가 호출되면 dummyCategory를 반환
        when(categoryRepository.findById(request.categoryId())).thenReturn(Optional.of(dummyCategory));
        // Mock 설정: productRepository.save가 호출되면 dummyProduct를 반환
        when(productRepository.save(any(Product.class))).thenReturn(dummyProduct);

        // when
        ProductDto.Response response = productService.createProduct(request,userEmail);

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
        String userEmail = "test@user.com";
        ProductDto.CreateRequest request = new ProductDto.CreateRequest("새 상품",  "새 설명", 15000,List.of("new.jpg"), 99L);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty()); // 존재하지 않는 카테고리 ID로 조회 시 빈 Optional 반환

        // when & then
        assertThrows(EntityNotFoundException.class, () -> productService.createProduct(request,userEmail));
        verify(productRepository, never()).save(any(Product.class)); // save 메소드가 호출되지 않았는지 검증
    }

    @Test
    @DisplayName("상품 수정 성공")
    void updateProduct_Success() {
        // given
        Long productId = 1L;
        String userEmail = "test@user.com";
        ProductDto.UpdateRequest request = new ProductDto.UpdateRequest("수정된 이름", "수정된 설명", 20000, List.of("updated.jpg"), 1L);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(dummyUser));
        when(productRepository.findById(productId)).thenReturn(Optional.of(dummyProduct));
        when(categoryRepository.findById(request.categoryId())).thenReturn(Optional.of(dummyCategory));

        // when
        ProductDto.Response response = productService.updateProduct(productId, request, userEmail);

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
        String userEmail = "test@user.com";

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(dummyUser));

        when(productRepository.findById(productId)).thenReturn(Optional.of(dummyProduct));
        // when
        productService.deleteProduct(productId, userEmail);

        // then
        verify(productRepository, times(1)).deleteById(productId);
    }
    
    @Test
    @DisplayName("전체 상품 목록 조회 (페이지네이션 적용)")
    void findProducts_FindAll_WithPagination() {
        // given
        // 1. 테스트용 Pageable 객체를 생성합니다. (0번째 페이지, 10개씩)
        Pageable pageable = PageRequest.of(0, 10);
        
        // 2. Mock Repository가 반환할 데이터 목록을 준비합니다.
        List<Product> productList = List.of(dummyProduct);
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());

        // 3. Mock 설정: findAll(Pageable)이 호출되면 위에서 만든 Page 객체를 반환하도록 설정합니다.
        when(productRepository.findAll(pageable)).thenReturn(productPage);

        // when
        // 4. 서비스 메소드 호출 시 Pageable 객체를 전달합니다.
        Page<ProductDto.Response> resultPage = productService.findProducts(null,null, pageable);

        // then
        // 5. 반환된 Page 객체의 내용을 검증합니다.
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getContent()).hasSize(1); // 현재 페이지의 내용물 개수
        assertThat(resultPage.getTotalElements()).isEqualTo(1); // 전체 항목 수
        assertThat(resultPage.getTotalPages()).isEqualTo(1); // 전체 페이지 수
    }

    @Test
    @DisplayName("카테고리별 상품 목록 조회 (페이지네이션 적용)")
    void findProducts_FindByCategory_WithPagination() {
        // given
        Long categoryId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> productList = List.of(dummyProduct);
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());

        // 3. Mock 설정: findByCategoryId(Pageable)가 호출되면 Page 객체를 반환하도록 설정합니다.
        when(productRepository.findByCategoryId(categoryId, pageable)).thenReturn(productPage);
        
        // when
        Page<ProductDto.Response> resultPage = productService.findProducts(null, categoryId, pageable);

        // then
        assertThat(resultPage.getContent()).hasSize(1);
        verify(productRepository, times(1)).findByCategoryId(categoryId, pageable);
    }

    @Test
    @DisplayName("키워드로 상품 목록 조회 (페이지네이션 적용)")
    void findProducts_SearchByKeyword_WithPagination() {
        // given
        String keyword = "노트북";
        Pageable pageable = PageRequest.of(0, 10);
        
        // Mock Repository가 반환할 데이터 준비
        List<Product> productList = List.of(dummyProduct);
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());

        // Mock 설정: findByNameContaining이 호출되면 준비된 Page 객체를 반환
        when(productRepository.findByNameContaining(keyword, pageable)).thenReturn(productPage);

        // when
        // 서비스 호출 시 keyword를 전달하고 categoryId는 null로 전달
        Page<ProductDto.Response> resultPage = productService.findProducts(keyword, null, pageable);

        // then
        assertThat(resultPage.getContent()).hasSize(1);
        
        // --- 핵심 검증 ---
        // findByNameContaining 메소드가 정확히 1번 호출되었는지 확인
        verify(productRepository, times(1)).findByNameContaining(keyword, pageable);
        // 다른 조회 메소드들은 호출되지 않았는지 확인
        verify(productRepository, never()).findAll(any(Pageable.class));
        verify(productRepository, never()).findByCategoryId(anyLong(), any(Pageable.class));
    }

    @Test
    @DisplayName("모든 카테고리 목록 조회 성공")
    void findAllCategories_Success() {
        // given
        // 1. Mock Repository가 반환할 가짜 카테고리 엔티티 목록을 생성합니다.
        List<Category> categoryList = List.of(
            new Category("카테고리1"),
            new Category("카테고리2")
        );
        // 2. Mock 설정: categoryRepository.findAll()이 호출되면 위에서 만든 리스트를 반환하도록 설정합니다.
        when(categoryRepository.findAll()).thenReturn(categoryList);

        // when
        // 3. 실제 서비스 메소드를 호출합니다.
        List<CategoryDto.Response> result = productService.findAllCategories();

        // then
        // 4. 반환된 DTO 리스트의 크기와 내용이 정확한지 검증합니다.
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("카테고리1");
        
        // 5. categoryRepository.findAll()이 정확히 1번 호출되었는지 검증합니다.
        verify(categoryRepository, times(1)).findAll();
    }
}