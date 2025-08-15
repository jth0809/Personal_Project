package com.personal.backend.dto;

import com.personal.backend.domain.Review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReviewDto {

    @Schema(name = "ReviewCreateRequest", description = "리뷰 생성 요청")
    public record CreateRequest(
        @NotNull(message = "상품 ID는 필수입니다.")
        Long productId,

        @NotNull(message = "별점은 필수입니다.")
        @Min(value = 1, message = "별점은 1점 이상이어야 합니다.")
        @Max(value = 5, message = "별점은 5점 이하이어야 합니다.")
        int rating,

        @NotBlank(message = "리뷰 내용은 필수입니다.")
        String comment
    ) {}

    @Schema(name = "ReviewUpdateRequest", description = "리뷰 수정 요청")
    public record UpdateRequest(
        @NotNull(message = "별점은 필수입니다.")
        @Min(value = 1, message = "별점은 1점 이상이어야 합니다.")
        @Max(value = 5, message = "별점은 5점 이하이어야 합니다.")
        int rating,

        @NotBlank(message = "리뷰 내용은 필수입니다.")
        String comment
    ) {}
    
    @Schema(name = "ReviewResponse", description = "리뷰 응답")
    public record Response(
            Long id,
            int rating,
            String comment,
            String authorName // 작성자 이름
    ) {
        public static Response fromEntity(Review review) {
            return new Response(
                    review.getId(),
                    review.getRating(),
                    review.getComment(),
                    review.getUser().getUsername()
            );
        }
    }
}
