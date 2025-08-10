package com.personal.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.backend.dto.UserDto;
import com.personal.backend.service.AuthService;
import com.personal.backend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; // import 추가
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // 👇 [수정] 이 어노테이션으로 모든 보안 필터를 비활성화합니다.
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("회원가입 API 호출 성공")
    void signup_Success() throws Exception {
        UserDto.SignupRequest request = new UserDto.SignupRequest("test@example.com", "password1234", "테스트유저");
        doNothing().when(userService).signup(any(UserDto.SignupRequest.class));

        // .with(csrf())는 이제 필터가 없으므로 필요 없습니다.
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("회원가입이 성공적으로 완료되었습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 API 호출 성공")
    void login_Success() throws Exception {
        UserDto.LoginRequest request = new UserDto.LoginRequest("test@example.com", "password1234");
        UserDto.TokenResponse tokenResponse = new UserDto.TokenResponse("dummy.jwt.token");
        when(authService.login(any(UserDto.LoginRequest.class))).thenReturn(tokenResponse);

        // .with(csrf())는 이제 필터가 없으므로 필요 없습니다.
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("dummy.jwt.token"));
    }
}