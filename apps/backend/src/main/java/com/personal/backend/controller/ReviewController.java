package com.personal.backend.controller;

import com.personal.backend.dto.PageableDto;
import com.personal.backend.dto.ReviewDto;
import com.personal.backend.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "리뷰 API", description = "상품 리뷰 조회, 작성, 수정, 삭제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "상품별 리뷰 목록 조회", description = "특정 상품에 달린 리뷰 목록을 페이지네이션으로 조회합니다.")
    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewDto.Response>> getReviews(
            @PathVariable Long productId,
            @ParameterObject PageableDto.PageableRequest pageableRequest
        ){
        Pageable pageable = pageableRequest.toPageable();
        Page<ReviewDto.Response> reviews = reviewService.getReviewsByProductId(productId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "리뷰 작성", description = "상품에 대한 새로운 리뷰를 작성합니다.")
    @PostMapping
    public ResponseEntity<ReviewDto.Response> createReview(
            @Valid @RequestBody ReviewDto.CreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        ReviewDto.Response newReview = reviewService.createReview(userEmail, request);
        return ResponseEntity.ok(newReview);
    }

    @Operation(summary = "리뷰 수정", description = "자신이 작성한 리뷰를 수정합니다.")
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDto.Response> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewDto.UpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        ReviewDto.Response updatedReview = reviewService.updateReview(reviewId, userEmail, request);
        return ResponseEntity.ok(updatedReview);
    }

    @Operation(summary = "리뷰 삭제", description = "자신이 작성한 리뷰를 삭제합니다.")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        reviewService.deleteReview(reviewId, userEmail);
        return ResponseEntity.noContent().build();
    }
}
