package com.personal.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.backend.dto.ProductDto;
import com.personal.backend.dto.ShippingInfoDto;
import com.personal.backend.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
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
    @WithMockUser(username = "test@user.com")
    @DisplayName("상품 목록 검색 API - 성공 (페이지네이션 적용)")
    void getAllProducts_SearchByKeyword_Success() throws Exception {
        // given
        String keyword = "노트북";
        ProductDto.Response productResponse = new ProductDto.Response(1L, "상품1", "설명1", 1000, 900, 10, List.of("img1.jpg"), "카테고리1", null, 0, 0, 0.0, 0.1, false, Collections.emptyList(), Collections.emptyList());
        Page<ProductDto.Response> responsePage = new PageImpl<>(List.of(productResponse));

        // Mock 설정: 서비스의 findProducts 메소드가 keyword와 함께 호출될 것을 예상
        when(productService.findProducts(eq(keyword), eq(null), any(Pageable.class), anyString()))
                .thenReturn(responsePage);

        // when & then
        // MockMvc 요청 시 URL에 keyword 파라미터를 추가
        mockMvc.perform(get("/products?keyword=노트북&page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(username = "test@user.com")
    @DisplayName("상품 목록 조회 API - 성공 (페이지네이션 적용)")
    void getAllProducts_Success_WithPagination() throws Exception {
        // given
        ProductDto.Response productResponse = new ProductDto.Response(1L, "상품1", "설명1", 1000, 900, 10, List.of("img1.jpg"), "카테고리1", null, 0, 0, 0.0, 0.1, false, Collections.emptyList(), Collections.emptyList());
        List<ProductDto.Response> responseList = List.of(productResponse);
        Page<ProductDto.Response> responsePage = new PageImpl<>(responseList);

        when(productService.findProducts(eq(null), eq(null), any(Pageable.class), anyString())).thenReturn(responsePage);

        // when & then
        mockMvc.perform(get("/products?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "test@user.com")
    @DisplayName("상품 생성 API - 성공 (ADMIN 권한)")
    void createProduct_Success_WithAdminRole() throws Exception {
        // given
        ProductDto.CreateRequest request = new ProductDto.CreateRequest("새 상품", "새 설명", 15000, List.of("new.jpg"), 1L, 10, 0.0);
        ProductDto.Response dummyResponse = new ProductDto.Response(1L, "새 상품", "새 설명", 15000, 13500, 10, List.of("new.jpg"), "카테고리1", null, 0, 0, 0.0, 0.1, false, Collections.emptyList(), Collections.emptyList());
        when(productService.createProduct(any(ProductDto.CreateRequest.class), anyString())).thenReturn(dummyResponse);

        // when & then
        mockMvc.perform(post("/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("상품 생성 API - 실패 (인증되지 않은 사용자)")
    void createProduct_Fail_Unauthorized() throws Exception {
        // given
        ProductDto.CreateRequest request = new ProductDto.CreateRequest("새 상품", "새 설명", 15000, List.of("new.jpg"), 1L, 10, 0.0);

        // when & then
        mockMvc.perform(post("/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "test@user.com")
    @DisplayName("상품 삭제 API - 성공 (ADMIN 권한)")
    void deleteProduct_Success_WithAdminRole() throws Exception {
        // given
        Long productId = 1L;
        String userEmail = "test@user.com";
        doNothing().when(productService).deleteProduct(productId, userEmail);

        // when & then
        mockMvc.perform(delete("/products/{productId}", productId)
                        .with(csrf()))
                .andExpect(status().isNoContent()); // 204 No Content 확인
    }

    @Test
    @WithMockUser(username = "test@user.com")
    @DisplayName("상품 상세 조회 API - 성공")
    void getProductById_Success() throws Exception {
        // given
        Long productId = 1L;
        String detailContent = "<p>이것은 상세 설명입니다.</p>";
        ProductDto.Response productResponse = new ProductDto.Response(productId, "상품1", "설명1", 1000, 900, 10, List.of("img1.jpg"), "카테고리1", detailContent, 0, 0, 0.0, 0.1, false, Collections.emptyList(), Collections.emptyList());

        when(productService.findProductById(eq(productId), anyString())).thenReturn(productResponse);

        // when & then
        mockMvc.perform(get("/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.detailContent").value(detailContent));
    }

    @Test
    @WithMockUser
    @DisplayName("상품 배송 정책 조회 API - 성공")
    void getProductShippingInfo_Success() throws Exception {
        // given
        Long productId = 1L;
        ShippingInfoDto.Response shippingResponse = new ShippingInfoDto.Response("택배", 3000, 50000, "2~3일", "CJ대한통운");

        when(productService.getShippingInfoByProductId(productId)).thenReturn(shippingResponse);

        // when & then
        mockMvc.perform(get("/products/{productId}/shipping", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shippingFee").value(3000))
                .andExpect(jsonPath("$.shippingProvider").value("CJ대한통운"));
    }

    @Test
    @WithMockUser
    @DisplayName("상품 배송 정책 조회 API - 실패 - 정보를 찾을 수 없음")
    void getProductShippingInfo_Fail_NotFound() throws Exception {
        // given
        Long productId = 99L;
        when(productService.getShippingInfoByProductId(productId)).thenThrow(new EntityNotFoundException("배송 정보를 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(get("/products/{productId}/shipping", productId))
                .andExpect(status().isNotFound());
    }
}