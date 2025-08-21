package com.personal.backend.graphql;

import com.personal.backend.config.jwt.JwtTokenProvider;
import com.personal.backend.domain.User;
import com.personal.backend.domain.UserRole;
import com.personal.backend.dto.UserDto;
import com.personal.backend.service.AuthService;
import com.personal.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.security.core.userdetails.UserDetailsService; // Added
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Added

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthGraphqlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean // Added
    private UserDetailsService userDetailsService;

    // 인증된 사용자를 위한 GraphQlTester
    private GraphQlTester authenticatedTester;

    // 인증되지 않은 사용자를 위한 GraphQlTester
    private GraphQlTester unauthenticatedTester;

    @BeforeEach
    void setUp() {
        // Mockito.mock()를 사용하여 User 클래스의 Mock 객체를 생성합니다.
        User mockUser = mock(User.class);
        
        // Mocking된 객체의 특정 메서드가 호출될 때 반환할 값을 정의합니다.
        // user.getId()가 호출되면 1L을 반환하도록 설정합니다.
        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getEmail()).thenReturn("user@test.com");
        when(mockUser.getUsername()).thenReturn("testuser");
        when(mockUser.getPassword()).thenReturn("password");
        when(mockUser.getRole()).thenReturn(UserRole.USER);

        // Changed to userService.findByEmail
        when(userService.findByEmail("user@test.com"))
                .thenReturn(Optional.of(mockUser));
                
        // Mock UserDetailsService for Spring Security
        when(userDetailsService.loadUserByUsername("user@test.com"))
                .thenReturn(new org.springframework.security.core.userdetails.User("user@test.com", "password", List.of(new SimpleGrantedAuthority("ROLE_USER"))));

        // `JwtTokenProvider`는 `@Autowired` 되었으므로 실제 토큰을 생성합니다.
        String token = jwtTokenProvider.createToken("user@test.com");

        WebTestClient authenticatedClient = MockMvcWebTestClient.bindTo(mockMvc)
                .baseUrl("/graphql")
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
        this.authenticatedTester = HttpGraphQlTester.create(authenticatedClient);

        // 비인증 클라이언트를 생성합니다.
        WebTestClient unauthenticatedClient = MockMvcWebTestClient.bindTo(mockMvc).baseUrl("/graphql").build();
        this.unauthenticatedTester = HttpGraphQlTester.create(unauthenticatedClient);
    }


    @Test
    @DisplayName("GraphQL Mutation: 회원가입 - 성공")
    void signup_success() {
        // Given
        Map<String, Object> input = Map.of("email", "newuser@test.com", "password", "password123", "username", "newuser");
        doNothing().when(userService).signup(any(UserDto.SignupRequest.class));
        // When & Then
        this.unauthenticatedTester.documentName("auth")
                .operationName("Signup")
                .variable("input", input)
                .execute()
                .path("signup")
                .entity(Boolean.class)
                .isEqualTo(true);
    }

    @Test
    @DisplayName("GraphQL Mutation: 로그인 - 성공")
    void login_success() {
        // Given
        Map<String, Object> input = Map.of("email", "user@test.com", "password", "password123");
        UserDto.TokenResponse tokenResponse = new UserDto.TokenResponse("fake-jwt-token");
        when(authService.login(any(UserDto.LoginRequest.class))).thenReturn(tokenResponse);
        // When & Then
        this.unauthenticatedTester.documentName("auth")
                .operationName("Login")
                .variable("input", input)
                .execute()
                .path("login.accessToken")
                .entity(String.class)
                .isEqualTo("fake-jwt-token");
    }

    @Test
    @DisplayName("GraphQL Query: 내 정보 조회 - 성공")
    void me_query_success() {
        // When & Then
        authenticatedTester.documentName("auth")
                .operationName("Me")
                .execute()
                .path("me.email").entity(String.class).isEqualTo("user@test.com")
                .path("me.username").entity(String.class).isEqualTo("testuser")
                .path("me.role").entity(String.class).isEqualTo(UserRole.USER.name());
    }

    @Test
    @DisplayName("GraphQL Query: 내 정보 조회 - 실패 (인증되지 않은 사용자)")
    void me_query_unauthenticated() {
        // When & Then
        unauthenticatedTester.documentName("auth")
                .operationName("Me")
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors).isNotEmpty();
                    assertThat(errors.get(0).getMessage()).contains("Access Denied");
                });
    }
}