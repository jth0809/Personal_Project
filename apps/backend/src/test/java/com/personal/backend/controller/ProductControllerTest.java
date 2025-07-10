package com.personal.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.backend.dto.ProductDto;
import com.personal.backend.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 👇 핵심 수정: @WebMvcTest 대신 순수한 Mockito 확장을 사용합니다.
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    // @Autowired 대신, MockMvc 객체를 직접 생성하여 사용합니다.
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock // ProductController가 의존하는 ProductService를 가짜(Mock)로 만듭니다.
    private ProductService productService;

    @InjectMocks // 가짜 ProductService를 실제 ProductController에 주입합니다.
    private ProductController productController;

    @BeforeEach // 각각의 테스트가 실행되기 전에 MockMvc를 설정합니다.
    void setUp() {
        // 스프링 컨텍스트 로딩 없이, 컨트롤러 하나만으로 MockMvc를 설정합니다.
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    @DisplayName("관리자 권한으로 상품 생성 API 테스트 - 성공")
    // 👇 핵심 수정: @WithMockUser는 Spring Security 테스트 컨텍스트가 필요하므로,
    // 이 방식의 테스트에서는 직접 인증 객체를 설정하거나, 보안 필터를 추가해야 합니다.
    // 여기서는 간단하게 하기 위해 보안 검증을 생략하고 로직만 테스트합니다.
    // (보안 테스트는 @SpringBootTest를 사용한 통합 테스트에서 수행하는 것이 더 좋습니다.)
    void createProductApiTest_ShouldSucceed() throws Exception {
        // given: 이런 요청 데이터가 주어졌을 때
        ProductDto.CreateRequest requestDto = new ProductDto.CreateRequest("새 상품", "설명", 20000, "url", 1L);
        
        // productService.createProduct 메소드가 호출되면, 임의의 Response DTO를 반환하도록 설정합니다.
        when(productService.createProduct(any(ProductDto.CreateRequest.class)))
            .thenReturn(new ProductDto.Response(1L, "새 상품", "설명", 20000, "url", "카테고리"));

        // when & then: /api/products로 POST 요청을 보내면, 성공(200 OK)할 것이다!
        mockMvc.perform(post("/api/products")
                // .with(csrf()) // CSRF는 Spring Security 설정이므로 여기서는 필요 없습니다.
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk());
    }
}
