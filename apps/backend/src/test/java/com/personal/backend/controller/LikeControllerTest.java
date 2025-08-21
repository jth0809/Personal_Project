package com.personal.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.backend.dto.LikeDto;
import com.personal.backend.dto.ProductDto;
import com.personal.backend.service.LikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeController.class)
@WithMockUser(username = "test@user.com")
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LikeService likeService;

    @Test
    @DisplayName("상품 찜하기 API 성공")
    void addLike_Success() throws Exception {
        // given
        LikeDto.LikeRequest request = new LikeDto.LikeRequest(100L);
        doNothing().when(likeService).addLike(eq("test@user.com"), eq(100L));

        // when & then
        mockMvc.perform(post("/api/likes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("찜 취소하기 API 성공")
    void removeLike_Success() throws Exception {
        // given
        Long productId = 100L;
        doNothing().when(likeService).removeLike(eq("test@user.com"), eq(productId));

        // when & then
        mockMvc.perform(delete("/api/likes/{productId}", productId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("내 찜 목록 조회 API 성공")
    void getMyLikes_Success() throws Exception {
        // given
        ProductDto.Response productResponse = new ProductDto.Response(
                100L, "Liked Product", "Desc", 20000, 18000, 5,
                List.of("image.jpg"), "Category", null, 1, 0, 0.0, 0.1, true, Collections.emptyList(), Collections.emptyList()
        );
        Page<ProductDto.Response> responsePage = new PageImpl<>(List.of(productResponse));

        when(likeService.getLikedProducts(eq("test@user.com"), any(Pageable.class))).thenReturn(responsePage);

        // when & then
        mockMvc.perform(get("/api/likes?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Liked Product"));
    }
}
