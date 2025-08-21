package com.personal.backend.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.backend.config.jwt.JwtTokenProvider;
import com.personal.backend.domain.Category;
import com.personal.backend.domain.Product;
import com.personal.backend.domain.Tag;
import com.personal.backend.domain.User;
import com.personal.backend.domain.UserRole;
import com.personal.backend.dto.CategoryDto;
import com.personal.backend.dto.ProductDto;
import com.personal.backend.graphql.dto.ProductInput;
import com.personal.backend.repository.UserRepository;
import com.personal.backend.service.CategoryService;
import com.personal.backend.service.LikeService;
import com.personal.backend.service.ProductService;
import com.personal.backend.service.TagService;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc // MockMvc를 사용하여 웹 환경을 모의합니다.
@AutoConfigureGraphQlTester // GraphQlTester를 자동 구성합니다.
@ActiveProfiles("test")
class ProductGraphqlControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private GraphQlTester graphQlTester;

    @MockitoBean
    private ProductService productService;
    @MockitoBean
    private LikeService likeService;
    @MockitoBean
    private TagService tagService;
    @MockitoBean
    private CategoryService categoryService;
    @MockitoBean
    private UserRepository userRepository;

    private User mockCurrentUser;
    private User mockAdminUser;
    private User mockProductOwner;
    private Category mockCategory;
    private ProductDto.Response mockProductDto;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // JWT 토큰을 사용하여 인증된 GraphQlTester 설정
        String adminEmail = "admin@test.com";
        mockAdminUser = User.builder().email(adminEmail).password("password").role(UserRole.ADMIN).build();
        when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.of(mockAdminUser));
        
        String token = jwtTokenProvider.createToken(adminEmail);
        
        WebTestClient client = MockMvcWebTestClient.bindTo(mockMvc)
                .baseUrl("/graphql")
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
        this.graphQlTester = HttpGraphQlTester.create(client);
        this.objectMapper = new ObjectMapper();

        // 일반 사용자 및 기타 Mock 객체 설정
        mockCurrentUser = User.builder().email("user@test.com").username("testuser").role(UserRole.USER).build();
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(mockCurrentUser));
        
        mockProductOwner = Mockito.mock(User.class);
        when(mockProductOwner.getId()).thenReturn(1L);
        when(mockProductOwner.getEmail()).thenReturn("productowner@test.com");
        when(mockProductOwner.getUsername()).thenReturn("productowner");
        when(mockProductOwner.getRole()).thenReturn(UserRole.USER);

        mockCategory = new Category("CategoryName");

        mockProductDto = new ProductDto.Response(
                1L, "Test Product", "Description", 10000, 9000, 10,
                List.of("image.jpg"), "CategoryName", "Detail Content", 5, 3, 4.5, 0.1, false, Collections.emptyList(), Collections.emptyList()
        );
    }
    
    //------------------------------------------------------------------------------------------------------------------
    // GraphQL Query Tests
    //------------------------------------------------------------------------------------------------------------------

    @Test
    @DisplayName("GraphQL Query: 상품 목록 조회 - 성공 (products)")
    void productsQuery_success() {
        // Given
        List<ProductDto.Response> productDtos = List.of(
            new ProductDto.Response(1L, "Product A", "Desc A", 100, 90, 5, List.of("img1.jpg"), "CategoryName", "Detail", 0, 0, 0.0, 0.0, false, Collections.emptyList(), Collections.emptyList()),
            new ProductDto.Response(2L, "Product B", "Desc B", 200, 180, 10, List.of("img2.jpg"), "CategoryName", "Detail", 0, 0, 0.0, 0.0, false, Collections.emptyList(), Collections.emptyList())
        );
        Page<ProductDto.Response> responsePage = new PageImpl<>(productDtos, PageRequest.of(0, 10), productDtos.size());
        
        when(productService.findProducts(any(), any(), any(), anyString())).thenReturn(responsePage);
        
        List<Product> mockProductsForBatch = productDtos.stream().map(dto -> {
            Product mockProduct = Mockito.mock(Product.class);
            when(mockProduct.getId()).thenReturn(dto.id());
            when(mockProduct.getUser()).thenReturn(mockProductOwner);
            when(mockProduct.getCategory()).thenReturn(mockCategory);
            return mockProduct;
        }).toList();

        when(productService.findCategoriesByNameIn(anySet())).thenReturn(List.of(mockCategory));
        when(productService.findProductsByIdIn(anySet())).thenReturn(mockProductsForBatch);

        // When & Then
        graphQlTester.documentName("product")
                .operationName("Products")
                .variable("keyword", "Test")
                .execute()
                .path("products.content[0].name").entity(String.class).isEqualTo("Product A")
                .path("products.content[0].category.name").entity(String.class).isEqualTo("CategoryName")
                .path("products.content[0].user.id").entity(String.class).isEqualTo("1");
    }

    @Test
    @DisplayName("GraphQL Query: 상품 상세 조회 - 성공 (product)")
    void productQueryById_success() {
        // Given
        when(productService.findProductById(anyLong(), anyString())).thenReturn(mockProductDto);
        
        Product mockProduct = Mockito.mock(Product.class);
        when(mockProduct.getId()).thenReturn(1L);
        when(mockProduct.getUser()).thenReturn(mockProductOwner);
        when(mockProduct.getCategory()).thenReturn(mockCategory);
        
        when(productService.findProductsByIdIn(anySet())).thenReturn(List.of(mockProduct));
        when(productService.findCategoriesByNameIn(anySet())).thenReturn(List.of(mockCategory));

        // When & Then
        graphQlTester.documentName("product")
                .operationName("Product")
                .variable("id", 1L)
                .execute()
                .path("product.name").entity(String.class).isEqualTo("Test Product")
                .path("product.user.id").entity(String.class).isEqualTo("1")
                .path("product.category.name").entity(String.class).isEqualTo("CategoryName");
    }

    @Test
    @DisplayName("GraphQL Query: 내 좋아요 목록 조회 - 성공 (myLikes)")
    void myLikesQuery_success() {
        // Given
        Page<ProductDto.Response> responsePage = new PageImpl<>(List.of(mockProductDto));
        when(likeService.getLikedProducts(anyString(), any())).thenReturn(responsePage);

        // When & Then
        graphQlTester.documentName("product")
                .operationName("MyLikes")
                .execute()
                .path("myLikes.content[0].name").entity(String.class).isEqualTo("Test Product");
    }

    @Test
    @DisplayName("GraphQL Query: 태그 목록 조회 - 성공 (tags)")
    void tagsQuery_success() {
        // Given
        Tag mockTag = new Tag("test-tag");
        when(tagService.findAllTags()).thenReturn(List.of(mockTag));
        
        // When & Then
        graphQlTester.documentName("product")
                .operationName("Tags")
                .execute()
                .path("tags[0].name").entity(String.class).isEqualTo("test-tag");
    }

    @Test
    @DisplayName("GraphQL Query: 카테고리 목록 조회 - 성공 (categories)")
    void categoriesQuery_success() {
        // Given
        CategoryDto.Response mockCategoryDto = new CategoryDto.Response(1L, "Category Name");
        when(productService.findAllCategories()).thenReturn(List.of(mockCategoryDto));

        // When & Then
        graphQlTester.documentName("product")
                .operationName("Categories")
                .execute()
                .path("categories[0].name").entity(String.class).isEqualTo("Category Name");
    }

    //------------------------------------------------------------------------------------------------------------------
    // GraphQL Mutation Tests
    //------------------------------------------------------------------------------------------------------------------

    @Test
    @DisplayName("GraphQL Mutation: 상품 생성 - 성공 (createProduct)")
    void createProductMutation_success() {
        // Given
        ProductInput input = new ProductInput("New Product", "Description", 10000, List.of("image.jpg"), 1L, 50, 0.0,"Detail");
        when(productService.createProduct(any(), anyString())).thenReturn(mockProductDto);

        // When & Then: ObjectMapper를 사용해 DTO를 Map으로 변환
        graphQlTester.documentName("product")
                .operationName("CreateProduct")
                .variable("input", objectMapper.convertValue(input, Map.class))
                .execute()
                .path("createProduct.name").entity(String.class).isEqualTo("Test Product");
    }

    @Test
    @DisplayName("GraphQL Mutation: 상품 수정 - 성공 (updateProduct)")
    void updateProductMutation_success() {
        // Given
        ProductDto.Response updatedDto = new ProductDto.Response(
            1L, "Updated Name", "Updated Desc", 10000, 9000, 10,
            List.of("image.jpg"), "CategoryName", "Detail Content", 5, 3, 4.5, 0.1, false, Collections.emptyList(), Collections.emptyList()
        );
        ProductInput input = new ProductInput("Updated Name", "Updated Desc", 10000, List.of("image.jpg"), 1L, 50, 0.0,"Detail");
        when(productService.updateProduct(anyLong(), any(), anyString())).thenReturn(updatedDto);

        // When & Then: ObjectMapper를 사용해 DTO를 Map으로 변환
        graphQlTester.documentName("product")
                .operationName("UpdateProduct")
                .variable("id", 1L)
                .variable("input", objectMapper.convertValue(input, Map.class))
                .execute()
                .path("updateProduct.name").entity(String.class).isEqualTo("Updated Name");
    }

    @Test
    @DisplayName("GraphQL Mutation: 상품 삭제 - 성공 (deleteProduct)")
    void deleteProductMutation_success() {
        // Given
        doNothing().when(productService).deleteProduct(anyLong(), anyString());

        // When & Then
        graphQlTester.documentName("product")
                .operationName("DeleteProduct")
                .variable("id", 1L)
                .execute()
                .path("deleteProduct").entity(Boolean.class).isEqualTo(true);
    }

    @Test
    @DisplayName("GraphQL Mutation: 좋아요 추가 - 성공 (addLike)")
    void addLikeMutation_success() {
        // Given
        Mockito.doNothing().when(likeService).addLike(anyString(), anyLong());
        when(productService.findProductById(anyLong(), anyString())).thenReturn(mockProductDto);

        // When & Then
        graphQlTester.documentName("product")
                .operationName("AddLike")
                .variable("productId", 1L)
                .execute()
                .path("addLike.id").entity(String.class).isEqualTo("1")
                .path("addLike.name").entity(String.class).isEqualTo("Test Product");
    }

    @Test
    @DisplayName("GraphQL Mutation: 좋아요 삭제 - 성공 (removeLike)")
    void removeLikeMutation_success() {
        // Given
        Mockito.doNothing().when(likeService).removeLike(anyString(), anyLong());
        when(productService.findProductById(anyLong(), anyString())).thenReturn(mockProductDto);

        // When & Then
        graphQlTester.documentName("product")
                .operationName("RemoveLike")
                .variable("productId", 1L)
                .execute()
                .path("removeLike.id").entity(String.class).isEqualTo("1")
                .path("removeLike.name").entity(String.class).isEqualTo("Test Product");
    }

    @Test
    @DisplayName("GraphQL Mutation: 태그 생성 - 성공 (createTag)")
    void createTagMutation_success() {
        // Given
        Tag mockTag = new Tag("New Tag");
        when(tagService.findOrCreate(anyString())).thenReturn(mockTag);

        // When & Then
        graphQlTester.documentName("product")
                .operationName("CreateTag")
                .variable("name", "New Tag")
                .execute()
                .path("createTag.name").entity(String.class).isEqualTo("New Tag");
    }

    @Test
    @DisplayName("GraphQL Mutation: 카테고리 생성 - 성공 (createCategory)")
    void createCategoryMutation_success() {
        // Given
        Category mockCategory = new Category("New Category");
        when(categoryService.createCategory(anyString())).thenReturn(mockCategory);

        // When & Then
        graphQlTester.documentName("product")
                .operationName("CreateCategory")
                .variable("name", "New Category")
                .execute()
                .path("createCategory.name").entity(String.class).isEqualTo("New Category");
    }

    //------------------------------------------------------------------------------------------------------------------
    // GraphQL Exception Handling Tests
    //------------------------------------------------------------------------------------------------------------------

    @Test
    @DisplayName("GraphQL Query: 상품 상세 조회 - 실패 (상품 없음)")
    void productQuery_fail_whenProductNotFound() {
        // Given
        String errorMessage = "ID 999에 해당하는 상품을 찾을 수 없습니다.";
        when(productService.findProductById(anyLong(), anyString()))
                .thenThrow(new EntityNotFoundException(errorMessage));

        // When & Then
        graphQlTester.documentName("product")
                .operationName("Product")
                .variable("id", 999L)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors).hasSize(1);
                    assertThat(errors.get(0).getMessage()).isEqualTo(errorMessage);
                    assertThat(errors.get(0).getErrorType().toString()).isEqualTo("NOT_FOUND");
                    assertThat(errors.get(0).getPath()).isEqualTo("product");
                });
    }
}