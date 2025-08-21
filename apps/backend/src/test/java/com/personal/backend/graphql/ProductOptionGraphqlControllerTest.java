package com.personal.backend.graphql;

import com.personal.backend.config.jwt.JwtTokenProvider;
import com.personal.backend.domain.User;
import com.personal.backend.domain.UserRole;
import com.personal.backend.dto.ProductOptionDto;
import com.personal.backend.graphql.dto.ProductOptionInput;
import com.personal.backend.repository.UserRepository;
import com.personal.backend.service.ProductOptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@SpringBootTest
@AutoConfigureMockMvc // ❗️ MockMvc를 사용하기 위한 자동 설정
@ActiveProfiles("test")
class ProductOptionGraphqlControllerTest {

    // ❗️ @Autowired를 제거하고, BeforeEach에서 수동으로 생성합니다.
    private GraphQlTester graphQlTester;

    // ❗️ GraphQlTester를 만들기 위한 기반으로 MockMvc를 주입받습니다.
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private ProductOptionService productOptionService;

    @MockitoBean
    private UserRepository userRepository;

    private String generateAdminToken() {
        return jwtTokenProvider.createToken("admin@test.com");
    }

    @BeforeEach
    void setUp() {
        when(userRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.of(User.builder().email("admin@test.com").password("password").role(UserRole.ADMIN).build()));

        String token = generateAdminToken();

        // 1. MockMvc를 WebTestClient로 변환하고 기본 헤더를 설정합니다.
        WebTestClient client = MockMvcWebTestClient.bindTo(mockMvc)
                .baseUrl("/graphql") // GraphQL 엔드포인트 경로
                .defaultHeader("Authorization", "Bearer " + token)
                .build();

        // 2. 헤더가 설정된 클라이언트로 GraphQlTester를 최종 생성합니다.
        this.graphQlTester = HttpGraphQlTester.create(client);
    }

    @Test
    @DisplayName("GraphQL Mutation: 상품 옵션 생성 - 성공 (ADMIN 권한)")
    void createProductOption_success() {
        // Given
        ProductOptionInput input = new ProductOptionInput("Color", "Red", 0, 10);
        ProductOptionDto.Response response = new ProductOptionDto.Response(1L, "Color", "Red", 0, 10);
        when(productOptionService.createOption(anyLong(), any(ProductOptionDto.CreateRequest.class))).thenReturn(response);

        // When & Then
        graphQlTester.documentName("product")
                .operationName("CreateProductOption")
                .variable("productId", 101L)
                .variable("input", Map.of("optionGroupName", input.optionGroupName(), "optionName", input.optionName(), "additionalPrice", input.additionalPrice(), "stockQuantity", input.stockQuantity()))
                .execute()
                .path("createProductOption.id").entity(String.class).isEqualTo("1")
                .path("createProductOption.optionName").entity(String.class).isEqualTo("Red");
    }

    @Test
    @DisplayName("GraphQL Mutation: 상품 옵션 생성 - 실패 (인증되지 않은 사용자)")
    void createProductOption_fail_unauthenticated() {
        // Given
        // ❗️ 인증이 없어야 하는 테스트를 위해, 헤더가 없는 임시 클라이언트를 별도로 생성합니다.
        WebTestClient unauthenticatedClient = MockMvcWebTestClient.bindTo(mockMvc)
                .baseUrl("/graphql").build();
        GraphQlTester unauthenticatedTester = HttpGraphQlTester.create(unauthenticatedClient);

        ProductOptionInput input = new ProductOptionInput("Color", "Red", 0, 10);

        // When & Then
        unauthenticatedTester.documentName("product")
                .operationName("CreateProductOption")
                .variable("productId", 101L)
                .variable("input", Map.of("optionGroupName", input.optionGroupName(), "optionName", input.optionName(), "additionalPrice", input.additionalPrice(), "stockQuantity", input.stockQuantity()))
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors).isNotEmpty();
                    assertThat(errors.get(0).getMessage()).contains("Access Denied");
                });
    }

    @Test
    @DisplayName("GraphQL Mutation: 상품 옵션 삭제 - 성공 (ADMIN 권한)")
    void deleteProductOption_success() {
        // Given
        doNothing().when(productOptionService).deleteOption(anyLong());

        // When & Then
        graphQlTester.documentName("product")
                .operationName("DeleteProductOption")
                .variable("optionId", 1L)
                .execute()
                .path("deleteProductOption").entity(Boolean.class).isEqualTo(true);
    }
}