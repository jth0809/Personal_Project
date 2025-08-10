package com.personal.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.backend.config.jwt.JwtTokenProvider;
import com.personal.backend.config.oauth.OAuth2AuthenticationSuccessHandler;
import com.personal.backend.dto.ProductDto;
import com.personal.backend.service.CustomOAuth2UserService;
import com.personal.backend.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;
    
    @Test
    @WithMockUser
    @DisplayName("상품 목록 검색 API - 성공 (페이지네이션 적용)")
    void getAllProducts_SearchByKeyword_Success() throws Exception {
        // given
        String keyword = "노트북";
        ProductDto.Response productResponse = new ProductDto.Response(1L, "상품1", "설명1", 1000, List.of("img1.jpg"), "카테고리1");
        Page<ProductDto.Response> responsePage = new PageImpl<>(List.of(productResponse));

        // Mock 설정: 서비스의 findProducts 메소드가 keyword와 함께 호출될 것을 예상
        when(productService.findProducts(eq(keyword), eq(null), any(Pageable.class)))
                .thenReturn(responsePage);

        // when & then
        // MockMvc 요청 시 URL에 keyword 파라미터를 추가
        mockMvc.perform(get("/products?keyword=노트북&page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser
    @DisplayName("상품 목록 조회 API - 성공 (페이지네이션 적용)")
    void getAllProducts_Success_WithPagination() throws Exception {
        // given
        // 1. Mock Service가 반환할 가짜 응답 DTO 목록과 Page 객체를 준비합니다.
        ProductDto.Response productResponse = new ProductDto.Response(1L, "상품1", "설명1", 1000, List.of("img1.jpg"), "카테고리1");
        List<ProductDto.Response> responseList = List.of(productResponse);
        Page<ProductDto.Response> responsePage = new PageImpl<>(responseList);

        // 2. Mock 설정: productService.findProducts가 호출되면 위에서 만든 Page 객체를 반환하도록 설정합니다.
        // any(Pageable.class)를 사용하여 어떤 Pageable 값이 들어와도 동작하도록 합니다.
        when(productService.findProducts(eq(null), eq(null), any(Pageable.class))).thenReturn(responsePage);

        // when & then
        // 3. MockMvc 요청 시 URL에 페이지 파라미터를 추가합니다.
        mockMvc.perform(get("/products?page=0&size=10"))
                .andExpect(status().isOk())
                // 4. JSON 응답 구조가 Page 형식에 맞는지 검증합니다.
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN") // 'ADMIN' 역할을 가진 사용자로 요청을 시뮬레이션
    @DisplayName("상품 생성 API - 성공 (ADMIN 권한)")
    void createProduct_Success_WithAdminRole() throws Exception {
        // given
        ProductDto.CreateRequest request = new ProductDto.CreateRequest("새 상품", "새 설명", 15000, List.of("new.jpg"), 1L);
        ProductDto.Response dummyResponse = new ProductDto.Response(1L, "새 상품", "새 설명", 15000, List.of("new.jpg"), "카테고리1");
        // createProduct는 void를 반환하므로 doNothing() 사용
        when(productService.createProduct(any(ProductDto.CreateRequest.class),anyString())).thenReturn(dummyResponse);

        // when & then
        mockMvc.perform(post("/products")
                        .with(csrf()) // POST 요청 시 CSRF 토큰 추가
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("상품 생성 API - 실패 (인증되지 않은 사용자)")
    void createProduct_Fail_Unauthorized() throws Exception {
        // given
        ProductDto.CreateRequest request = new ProductDto.CreateRequest("새 상품", "새 설명", 15000, List.of("new.jpg"), 1L);
        
        // when & then
        // @WithMockUser가 없으므로 인증되지 않은 사용자의 요청이 됩니다.
        // Spring Security는 보통 401 Unauthorized 또는 403 Forbidden을 반환합니다.
        mockMvc.perform(post("/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("상품 삭제 API - 성공 (ADMIN 권한)")
    void deleteProduct_Success_WithAdminRole() throws Exception {
        // given
        Long productId = 1L;
        String userEmail = "test@user.com";
        doNothing().when(productService).deleteProduct(productId,userEmail);
        
        // when & then
        mockMvc.perform(delete("/products/{id}", productId)
                        .with(csrf()))
                .andExpect(status().isNoContent()); // 204 No Content 확인
    }
}