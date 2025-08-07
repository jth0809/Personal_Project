package com.personal.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.backend.dto.ProductDto;
import com.personal.backend.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @Test
    @WithMockUser
    @DisplayName("상품 목록 조회 API - 성공 (공개 접근)")
    void getAllProducts_Success() throws Exception {
        // given
        List<ProductDto.Response> productList = List.of(
            new ProductDto.Response(1L, "상품1", "설명1", 1000, List.of("img1.jpg"), "카테고리1"),
            new ProductDto.Response(2L, "상품2", "설명2", 2000, List.of("img2.jpg"), "카테고리2")
        );
        when(productService.findProducts(null)).thenReturn(productList);

        // when & then
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("상품1"));
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