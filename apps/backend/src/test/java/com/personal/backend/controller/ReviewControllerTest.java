package com.personal.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.backend.dto.ReviewDto;
import com.personal.backend.service.ReviewService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@WithMockUser(username = "test@user.com")
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

    @Test
    @DisplayName("상품별 리뷰 목록 조회 API - 성공")
    void getReviews_Success() throws Exception {
        // given
        Long productId = 1L;
        ReviewDto.Response reviewResponse = new ReviewDto.Response(1L, 5, "아주 좋아요!", "작성자");
        Page<ReviewDto.Response> responsePage = new PageImpl<>(List.of(reviewResponse));

        when(reviewService.getReviewsByProductId(eq(productId), any(Pageable.class))).thenReturn(responsePage);

        // when & then
        mockMvc.perform(get("/reviews/product/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.content[0].rating").value(5));
    }

    @Test
    @DisplayName("리뷰 작성 API - 성공")
    void createReview_Success() throws Exception {
        // given
        ReviewDto.CreateRequest request = new ReviewDto.CreateRequest(1L, 5, "새 리뷰 내용");
        ReviewDto.Response response = new ReviewDto.Response(1L, 5, "새 리뷰 내용", "test@user.com");

        when(reviewService.createReview(eq("test@user.com"), any(ReviewDto.CreateRequest.class))).thenReturn(response);

        // when & then
        mockMvc.perform(post("/reviews")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("새 리뷰 내용"));
    }

    @Test
    @DisplayName("리뷰 수정 API - 성공")
    void updateReview_Success() throws Exception {
        // given
        Long reviewId = 1L;
        ReviewDto.UpdateRequest request = new ReviewDto.UpdateRequest(4, "수정된 리뷰 내용");
        ReviewDto.Response response = new ReviewDto.Response(reviewId, 4, "수정된 리뷰 내용", "test@user.com");

        when(reviewService.updateReview(eq(reviewId), eq("test@user.com"), any(ReviewDto.UpdateRequest.class))).thenReturn(response);

        // when & then
        mockMvc.perform(put("/reviews/{reviewId}", reviewId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(4));
    }

    @Test
    @DisplayName("리뷰 삭제 API - 성공")
    void deleteReview_Success() throws Exception {
        // given
        Long reviewId = 1L;
        doNothing().when(reviewService).deleteReview(reviewId, "test@user.com");

        // when & then
        mockMvc.perform(delete("/reviews/{reviewId}", reviewId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
