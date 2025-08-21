package com.personal.backend.graphql;

import com.personal.backend.config.jwt.JwtTokenProvider;
import com.personal.backend.domain.User;
import com.personal.backend.domain.UserRole;
import com.personal.backend.dto.QnaDto;
import com.personal.backend.dto.ReviewDto;
import com.personal.backend.graphql.dto.QnaInput;
import com.personal.backend.graphql.dto.ReviewInput;
import com.personal.backend.repository.UserRepository;
import com.personal.backend.service.QnaService;
import com.personal.backend.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CommunityGraphqlControllerTest {

    private GraphQlTester graphQlTester; // USER 권한
    private GraphQlTester adminGraphQlTester; // ADMIN 권한

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private ReviewService reviewService;
    @MockitoBean
    private QnaService qnaService;
    @MockitoBean
    private UserRepository userRepository;

    // 토큰 생성 메서드를 분리하여 재사용성 높임
    private String generateToken(String email) {
        return jwtTokenProvider.createToken(email);
    }

    // GraphQlTester 생성 메서드를 분리
    private GraphQlTester createGraphQlTester(String token) {
        WebTestClient client = MockMvcWebTestClient.bindTo(mockMvc)
                .baseUrl("/graphql")
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
        return HttpGraphQlTester.create(client);
    }

    @BeforeEach
    void setUp() {
        // Mock User 데이터 설정
        User user = User.builder().email("user@test.com").username("user").password("password").role(UserRole.USER).build();
        User adminUser = User.builder().email("admin@test.com").username("admin").password("password").role(UserRole.ADMIN).build();
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminUser));

        // 각 권한에 맞는 GraphQlTester 생성
        this.graphQlTester = createGraphQlTester(generateToken("user@test.com"));
        this.adminGraphQlTester = createGraphQlTester(generateToken("admin@test.com"));
    }
    
    // --- 이하 테스트 코드는 변경할 필요 없음 ---
    
    @Test
    @DisplayName("GraphQL Query: 리뷰 목록 조회 - 성공 (필드 리졸버 포함)")
    void reviews_query_success() {
        // Given
        ReviewDto.Response review = new ReviewDto.Response(1L, 5, "Great!", "user@test.com");
        when(reviewService.getReviewsByProductId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(review)));
    
        User mockAuthor = User.builder().email("user@test.com").build();
        when(userRepository.findByEmailIn(anySet())).thenReturn(List.of(mockAuthor));

        // When & Then
        graphQlTester.documentName("community")
                .operationName("Reviews")
                .variable("productId", 101L)
                .execute()
                .path("reviews.content[0].id").entity(String.class).isEqualTo("1")
                .path("reviews.content[0].author.email").entity(String.class).isEqualTo("user@test.com");
        
        verify(reviewService).getReviewsByProductId(anyLong(), any(Pageable.class));
        verify(userRepository).findByEmailIn(anySet());
    }
    
    @Test
    @DisplayName("GraphQL Query: Q&A 목록 조회 - 성공 (필드 리졸버 포함)")
    void qnas_query_success() {
        // Given
        QnaDto.Response qna = new QnaDto.Response(1L, "Question?", null, "user@test.com", LocalDateTime.now(), null);
        when(qnaService.getQnaByProductId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(qna)));
    
        User mockAuthor = User.builder().email("user@test.com").build();
        when(userRepository.findByEmailIn(anySet())).thenReturn(List.of(mockAuthor));

        // When & Then
        graphQlTester.documentName("community")
                .operationName("Qnas")
                .variable("productId", 101L)
                .execute()
                .path("qnas.content[0].id").entity(String.class).isEqualTo("1")
                .path("qnas.content[0].author.email").entity(String.class).isEqualTo("user@test.com");
    
        verify(qnaService).getQnaByProductId(anyLong(), any(Pageable.class));
        verify(userRepository).findByEmailIn(anySet());
    }
    
    @Test
    @DisplayName("GraphQL Mutation: 리뷰 작성 - 성공")
    void createReview_mutation_success() {
        // Given
        ReviewInput input = new ReviewInput(101L, 5, "Awesome!");
        ReviewDto.Response response = new ReviewDto.Response(1L, 5, "Awesome!", "user@test.com");
        when(reviewService.createReview(anyString(), any())).thenReturn(response);
 
        // When & Then
        graphQlTester.documentName("community")
                .operationName("CreateReview")
                .variable("input", input)
                .execute()
                .path("createReview.id").entity(String.class).isEqualTo("1")
                .path("createReview.rating").entity(Integer.class).isEqualTo(5)
                .path("createReview.comment").entity(String.class).isEqualTo("Awesome!");
    
        verify(reviewService).createReview(anyString(), any(ReviewDto.CreateRequest.class));
    }

    @Test
    @DisplayName("GraphQL Mutation: 리뷰 수정 - 성공")
    void updateReview_mutation_success() {
        // Given
        ReviewInput input = new ReviewInput(101L, 4, "Updated comment.");
        ReviewDto.Response response = new ReviewDto.Response(1L, 4, "Updated comment.", "user@test.com");
        when(reviewService.updateReview(anyLong(), anyString(), any())).thenReturn(response);

        // When & Then
        graphQlTester.documentName("community")
                .operationName("UpdateReview")
                .variable("reviewId", 1L)
                .variable("input", input)
                .execute()
                .path("updateReview.id").entity(String.class).isEqualTo("1")
                .path("updateReview.rating").entity(Integer.class).isEqualTo(4)
                .path("updateReview.comment").entity(String.class).isEqualTo("Updated comment.");
    
        verify(reviewService).updateReview(anyLong(), anyString(), any(ReviewDto.UpdateRequest.class));
    }
    
    @Test
    @DisplayName("GraphQL Mutation: 리뷰 삭제 - 성공")
    void deleteReview_mutation_success() {
        // Given
        doNothing().when(reviewService).deleteReview(anyLong(), anyString());
    
        // When & Then
        graphQlTester.documentName("community")
                .operationName("DeleteReview")
                .variable("reviewId", 1L)
                .execute()
                .path("deleteReview").entity(Boolean.class).isEqualTo(true);
    
        verify(reviewService).deleteReview(anyLong(), anyString());
    }
    
    @Test
    @DisplayName("GraphQL Mutation: Q&A 작성 - 성공")
    void createQna_mutation_success() {
        // Given
        QnaInput input = new QnaInput(101L, "Is this product durable?");
        QnaDto.Response response = new QnaDto.Response(1L, "Is this product durable?", null, "user@test.com", LocalDateTime.now(), null);
        when(qnaService.createQuestion(anyString(), any())).thenReturn(response);

        // When & Then
        graphQlTester.documentName("community")
                .operationName("CreateQna")
                .variable("input", input)
                .execute()
                .path("createQna.id").entity(String.class).isEqualTo("1");
    
        verify(qnaService).createQuestion(anyString(), any(QnaDto.CreateRequest.class));
    }
    
    @Test
    @DisplayName("GraphQL Mutation: Q&A 답변 작성 - 성공 (ADMIN 권한)")
    void answerQna_mutation_success() {
        // Given
        QnaDto.Response qnaResponse = new QnaDto.Response(1L, "Question?", "Answer!", "admin@test.com", LocalDateTime.now(), LocalDateTime.now());
        when(qnaService.addAnswer(anyLong(), any(QnaDto.AnswerRequest.class))).thenReturn(qnaResponse);
    
        User mockAuthor = User.builder().email("admin@test.com").build();
        when(userRepository.findByEmailIn(anySet())).thenReturn(List.of(mockAuthor));
    
        // When & Then
        adminGraphQlTester.documentName("community")
                .operationName("AnswerQna")
                .variable("qnaId", 1L)
                .variable("answer", "Answer!")
                .execute()
                .path("answerQna.answer").entity(String.class).isEqualTo("Answer!")
                .path("answerQna.author.email").entity(String.class).isEqualTo("admin@test.com");
    
        verify(qnaService).addAnswer(anyLong(), any(QnaDto.AnswerRequest.class));
    }
    
    @Test
    @DisplayName("GraphQL Mutation: Q&A 삭제 - 성공")
    void deleteQna_mutation_success() {
        // Given
        doNothing().when(qnaService).deleteQna(anyLong(), anyString());
    
        // When & Then
        graphQlTester.documentName("community")
                .operationName("DeleteQna")
                .variable("qnaId", 1L)
                .execute()
                .path("deleteQna").entity(Boolean.class).isEqualTo(true);
    
        verify(qnaService).deleteQna(anyLong(), anyString());
    }
}