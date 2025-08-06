package com.personal.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.backend.dto.ImageDto;
import com.personal.backend.config.oci.OciUploadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageController.class)
@AutoConfigureMockMvc(addFilters = false) // 보안 필터 비활성화로 401 오류 방지
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean // 실제 서비스 대신 가짜 OciUploadService를 사용
    private OciUploadService ociUploadService;

    @Test
    @DisplayName("이미지 업로드 URL 생성 API 성공")
    void generateUploadUrl_Success() throws Exception {
        // given: 클라이언트가 보낼 요청 데이터
        ImageDto.GenerateUploadUrlRequest request = new ImageDto.GenerateUploadUrlRequest("test-image.jpg");

        // given: 서비스가 반환할 가짜 응답 데이터
        ImageDto.GenerateUploadUrlResponse response = new ImageDto.GenerateUploadUrlResponse(
                "http://temp-upload-url.com/upload-here",
                "http://final-image-url.com/view-here"
        );

        // Mock 설정: ociUploadService의 메소드가 호출되면 위에서 만든 가짜 응답을 반환하도록 설정
        when(ociUploadService.generatePreAuthenticatedUploadUrl(any(ImageDto.GenerateUploadUrlRequest.class)))
                .thenReturn(response);

        // when & then: POST /images/generate-upload-url 요청을 시뮬레이션하고 결과를 검증
        mockMvc.perform(post("/images/generate-upload-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // HTTP 상태 코드가 200 OK인지 확인
                .andExpect(jsonPath("$.uploadUrl").value(response.uploadUrl())) // JSON 응답의 uploadUrl 필드 검증
                .andExpect(jsonPath("$.imageUrl").value(response.imageUrl())); // JSON 응답의 imageUrl 필드 검증
        
        // then: ociUploadService의 메소드가 정확히 1번 호출되었는지 검증
        verify(ociUploadService, times(1)).generatePreAuthenticatedUploadUrl(any(ImageDto.GenerateUploadUrlRequest.class));
    }
}