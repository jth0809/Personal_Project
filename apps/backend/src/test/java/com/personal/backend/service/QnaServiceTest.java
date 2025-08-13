package com.personal.backend.service;

import com.personal.backend.domain.Product;
import com.personal.backend.domain.Qna;
import com.personal.backend.domain.User;
import com.personal.backend.dto.QnaDto;
import com.personal.backend.repository.ProductRepository;
import com.personal.backend.repository.QnaRepository;
import com.personal.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QnaServiceTest {

    @InjectMocks
    private QnaService qnaService;

    @Mock
    private QnaRepository qnaRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    private User dummyUser;
    private Product dummyProduct;
    private Qna dummyQna;

    @BeforeEach
    void setUp() {
        dummyUser = User.builder().email("test@user.com").build();
        try {
            var idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(dummyUser, 1L);
        } catch (Exception e) { e.printStackTrace(); }

        dummyProduct = Product.builder().name("Test Product").build();
        try {
            var idField = Product.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(dummyProduct, 1L);
        } catch (Exception e) { e.printStackTrace(); }

        dummyQna = Qna.builder()
                .user(dummyUser)
                .product(dummyProduct)
                .question("Is this a test?")
                .build();
    }

    @Test
    @DisplayName("상품 ID로 Q&A 목록 조회 성공")
    void getQnaByProductId_Success() {
        Long productId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Qna> qnaList = List.of(dummyQna);
        Page<Qna> qnaPage = new PageImpl<>(qnaList, pageable, qnaList.size());

        when(qnaRepository.findByProductId(productId, pageable)).thenReturn(qnaPage);

        Page<QnaDto.Response> result = qnaService.getQnaByProductId(productId, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).question()).isEqualTo("Is this a test?");
        verify(qnaRepository, times(1)).findByProductId(productId, pageable);
    }

    @Test
    @DisplayName("질문 생성 성공")
    void createQuestion_Success() {
        String userEmail = "test@user.com";
        QnaDto.CreateRequest request = new QnaDto.CreateRequest(1L, "New Question");

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(dummyUser));
        when(productRepository.findById(request.productId())).thenReturn(Optional.of(dummyProduct));
        when(qnaRepository.save(any(Qna.class))).thenReturn(dummyQna);

        QnaDto.Response response = qnaService.createQuestion(userEmail, request);

        assertThat(response).isNotNull();
        assertThat(response.question()).isEqualTo(dummyQna.getQuestion());
        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(productRepository, times(1)).findById(request.productId());
        verify(qnaRepository, times(1)).save(any(Qna.class));
    }

    @Test
    @DisplayName("질문 생성 실패 - 사용자를 찾을 수 없음")
    void createQuestion_Fail_UserNotFound() {
        String userEmail = "nonexistent@user.com";
        QnaDto.CreateRequest request = new QnaDto.CreateRequest(1L, "New Question");

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> qnaService.createQuestion(userEmail, request));
        verify(productRepository, never()).findById(anyLong());
        verify(qnaRepository, never()).save(any(Qna.class));
    }

    @Test
    @DisplayName("답변 추가 성공")
    void addAnswer_Success() {
        Long qnaId = 1L;
        QnaDto.AnswerRequest request = new QnaDto.AnswerRequest("This is an answer.");

        when(qnaRepository.findById(qnaId)).thenReturn(Optional.of(dummyQna));

        QnaDto.Response response = qnaService.addAnswer(qnaId, request);

        assertThat(response).isNotNull();
        assertThat(response.answer()).isEqualTo("This is an answer.");
        verify(qnaRepository, times(1)).findById(qnaId);
    }

    @Test
    @DisplayName("Q&A 삭제 성공")
    void deleteQna_Success() {
        Long qnaId = 1L;
        String userEmail = "test@user.com";

        when(qnaRepository.findById(qnaId)).thenReturn(Optional.of(dummyQna));
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(dummyUser));

        qnaService.deleteQna(qnaId, userEmail);

        verify(qnaRepository, times(1)).findById(qnaId);
        verify(userRepository, times(1)).findByEmail(userEmail);
        verify(qnaRepository, times(1)).delete(dummyQna);
    }

    @Test
    @DisplayName("Q&A 삭제 실패 - 권한 없음")
    void deleteQna_Fail_NoPermission() {
        Long qnaId = 1L;
        String anotherUserEmail = "another@user.com";
        User anotherUser = User.builder().email(anotherUserEmail).build();
        try {
            var idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(anotherUser, 2L);
        } catch (Exception e) { e.printStackTrace(); }


        when(qnaRepository.findById(qnaId)).thenReturn(Optional.of(dummyQna));
        when(userRepository.findByEmail(anotherUserEmail)).thenReturn(Optional.of(anotherUser));

        assertThrows(SecurityException.class, () -> qnaService.deleteQna(qnaId, anotherUserEmail));
        verify(qnaRepository, never()).delete(any(Qna.class));
    }
}
