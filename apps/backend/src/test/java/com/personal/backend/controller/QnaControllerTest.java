package com.personal.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.backend.dto.QnaDto;
import com.personal.backend.service.QnaService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QnaController.class)
@WithMockUser(username = "test@user.com")
class QnaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private QnaService qnaService;

    @Test
    @DisplayName("상품별 Q&A 목록 조회 API - 성공")
    void getQna_Success() throws Exception {
        // given
        Long productId = 1L;
        QnaDto.Response qnaResponse = new QnaDto.Response(1L, "이거 재고 있나요?", null, "작성자", LocalDateTime.now(), null);
        Page<QnaDto.Response> responsePage = new PageImpl<>(List.of(qnaResponse));

        when(qnaService.getQnaByProductId(eq(productId), any(Pageable.class))).thenReturn(responsePage);

        // when & then
        mockMvc.perform(get("/qna/product/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.content[0].question").value("이거 재고 있나요?"));
    }

    @Test
    @DisplayName("질문 작성 API - 성공")
    void createQuestion_Success() throws Exception {
        // given
        QnaDto.CreateRequest request = new QnaDto.CreateRequest(1L, "새로운 질문입니다.");
        QnaDto.Response response = new QnaDto.Response(1L, "새로운 질문입니다.", null, "test@user.com", LocalDateTime.now(), null);

        when(qnaService.createQuestion(eq("test@user.com"), any(QnaDto.CreateRequest.class))).thenReturn(response);

        // when & then
        mockMvc.perform(post("/qna/questions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question").value("새로운 질문입니다."));
    }

    @Test
    @WithMockUser(roles = "ADMIN") // 관리자 권한으로 테스트
    @DisplayName("답변 작성 API - 성공 (관리자)")
    void addAnswer_Success() throws Exception {
        // given
        Long qnaId = 1L;
        QnaDto.AnswerRequest request = new QnaDto.AnswerRequest("네, 재고 있습니다.");
        QnaDto.Response response = new QnaDto.Response(qnaId, "이거 재고 있나요?", "네, 재고 있습니다.", "test@user.com", LocalDateTime.now(), LocalDateTime.now());

        when(qnaService.addAnswer(eq(qnaId), any(QnaDto.AnswerRequest.class))).thenReturn(response);

        // when & then
        mockMvc.perform(put("/qna/{qnaId}/answers", qnaId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("네, 재고 있습니다."));
    }

    @Test
    @DisplayName("Q&A 삭제 API - 성공")
    void deleteQna_Success() throws Exception {
        // given
        Long qnaId = 1L;
        doNothing().when(qnaService).deleteQna(qnaId, "test@user.com");

        // when & then
        mockMvc.perform(delete("/qna/{qnaId}", qnaId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
