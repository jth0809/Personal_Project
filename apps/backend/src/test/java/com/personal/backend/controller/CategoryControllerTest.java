package com.personal.backend.controller;

import com.personal.backend.dto.CategoryDto;
import com.personal.backend.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false) // 보안 필터 비활성화
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService; // CategoryController가 의존하는 Service

    @Test
    @DisplayName("모든 카테고리 목록 조회 API 성공")
    void getAllCategories_Success() throws Exception {
        // given
        // 1. Mock Service가 반환할 가짜 DTO 목록을 생성합니다.
        List<CategoryDto.Response> dtoList = List.of(
                new CategoryDto.Response(1L, "전자제품"),
                new CategoryDto.Response(2L, "의류")
        );
        // 2. Mock 설정: productService.findAllCategories()가 호출되면 위 리스트를 반환하도록 설정합니다.
        when(productService.findAllCategories()).thenReturn(dtoList);

        // when & then
        // 3. MockMvc로 GET /categories API를 호출하고 응답을 검증합니다.
        mockMvc.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 200 OK 상태인지 확인
                .andExpect(jsonPath("$").isArray()) // 응답이 배열인지 확인
                .andExpect(jsonPath("$.size()").value(2)) // 배열의 크기가 2인지 확인
                .andExpect(jsonPath("$[0].name").value("전자제품")); // 첫 번째 요소의 name 필드 값 확인
    }
}