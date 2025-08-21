package com.personal.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.backend.dto.ProductOptionDto;
import com.personal.backend.service.ProductOptionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductOptionController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductOptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductOptionService productOptionService;

    @Test
    @DisplayName("상품 옵션 생성 API 성공")
    void createOption_Success() throws Exception {
        // given
        Long productId = 1L;
        ProductOptionDto.CreateRequest request = new ProductOptionDto.CreateRequest("사이즈", "L", 0, 50);
        ProductOptionDto.Response response = new ProductOptionDto.Response(1L, "사이즈", "L", 0, 50);

        when(productOptionService.createOption(eq(productId), any(ProductOptionDto.CreateRequest.class))).thenReturn(response);

        // when & then
        mockMvc.perform(post("/products/{productId}/options", productId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.optionName").value("L"))
                .andExpect(jsonPath("$.stockQuantity").value(50));
    }

    @Test
    @DisplayName("상품 옵션 삭제 API 성공")
    void deleteOption_Success() throws Exception {
        // given
        Long productId = 1L;
        Long optionId = 10L;
        doNothing().when(productOptionService).deleteOption(optionId);

        // when & then
        mockMvc.perform(delete("/products/{productId}/options/{optionId}", productId, optionId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
