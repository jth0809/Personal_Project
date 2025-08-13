package com.personal.backend.service;

import com.personal.backend.domain.Product;
import com.personal.backend.domain.Review;
import com.personal.backend.domain.User;
import com.personal.backend.dto.ReviewDto;
import com.personal.backend.repository.ProductRepository;
import com.personal.backend.repository.ReviewRepository;
import com.personal.backend.repository.UserRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    private User dummyUser;
    private Product dummyProduct;
    private Review dummyReview;

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

        dummyReview = Review.builder()
                .user(dummyUser)
                .product(dummyProduct)
                .rating(5)
                .comment("Great product!")
                .build();
    }

    @Test
    @DisplayName("상품 ID로 리뷰 목록 조회 성공")
    void getReviewsByProductId_Success() {
        Long productId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Review> reviewList = List.of(dummyReview);
        Page<Review> reviewPage = new PageImpl<>(reviewList, pageable, reviewList.size());

        when(reviewRepository.findByProductId(productId, pageable)).thenReturn(reviewPage);

        Page<ReviewDto.Response> result = reviewService.getReviewsByProductId(productId, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).comment()).isEqualTo("Great product!");
        verify(reviewRepository, times(1)).findByProductId(productId, pageable);
    }

    @Test
    @DisplayName("리뷰 생성 성공")
    void createReview_Success() {
        String userEmail = "test@user.com";
        ReviewDto.CreateRequest request = new ReviewDto.CreateRequest(1L, 5, "Excellent!");

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(dummyUser));
        when(productRepository.findById(request.productId())).thenReturn(Optional.of(dummyProduct));
        when(reviewRepository.save(any(Review.class))).thenReturn(dummyReview);

        ReviewDto.Response response = reviewService.createReview(userEmail, request);

        assertThat(response).isNotNull();
        assertThat(response.comment()).isEqualTo(dummyReview.getComment());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void updateReview_Success() {
        Long reviewId = 1L;
        String userEmail = "test@user.com";
        ReviewDto.UpdateRequest request = new ReviewDto.UpdateRequest(4, "Good product.");

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(dummyReview));
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(dummyUser));

        ReviewDto.Response response = reviewService.updateReview(reviewId, userEmail, request);

        assertThat(response).isNotNull();
        assertThat(response.rating()).isEqualTo(4);
        assertThat(response.comment()).isEqualTo("Good product.");
        verify(reviewRepository, times(1)).findById(reviewId);
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void deleteReview_Success() {
        Long reviewId = 1L;
        String userEmail = "test@user.com";

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(dummyReview));
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(dummyUser));

        reviewService.deleteReview(reviewId, userEmail);

        verify(reviewRepository, times(1)).delete(dummyReview);
    }

    @Test
    @DisplayName("리뷰 삭제 실패 - 권한 없음")
    void deleteReview_Fail_NoPermission() {
        Long reviewId = 1L;
        String anotherUserEmail = "another@user.com";
        User anotherUser = User.builder().email(anotherUserEmail).build();
        try {
            var idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(anotherUser, 2L);
        } catch (Exception e) { e.printStackTrace(); }

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(dummyReview));
        when(userRepository.findByEmail(anotherUserEmail)).thenReturn(Optional.of(anotherUser));

        assertThrows(SecurityException.class, () -> reviewService.deleteReview(reviewId, anotherUserEmail));
        verify(reviewRepository, never()).delete(any(Review.class));
    }
}
