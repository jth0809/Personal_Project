package com.personal.backend.service;

import com.personal.backend.domain.Product;
import com.personal.backend.domain.Review;
import com.personal.backend.domain.User;
import com.personal.backend.dto.ReviewDto;
import com.personal.backend.repository.ProductRepository;
import com.personal.backend.repository.ReviewRepository;
import com.personal.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<ReviewDto.Response> getReviewsByProductId(Long productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable)
                .map(ReviewDto.Response::fromEntity);
    }

    public ReviewDto.Response createReview(String userEmail, ReviewDto.CreateRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(request.rating())
                .comment(request.comment())
                .build();

        Review savedReview = reviewRepository.save(review);
        return ReviewDto.Response.fromEntity(savedReview);
    }

    public ReviewDto.Response updateReview(Long reviewId, String userEmail, ReviewDto.UpdateRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new SecurityException("리뷰를 수정할 권한이 없습니다.");
        }

        review.update(request.rating(), request.comment());
        return ReviewDto.Response.fromEntity(review);
    }

    public void deleteReview(Long reviewId, String userEmail) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new SecurityException("리뷰를 삭제할 권한이 없습니다.");
        }

        reviewRepository.delete(review);
    }
}
