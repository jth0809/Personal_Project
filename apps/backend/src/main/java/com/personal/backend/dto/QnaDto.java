package com.personal.backend.dto;

import com.personal.backend.domain.Qna;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class QnaDto {

    public record CreateRequest(
        @NotNull(message = "상품 ID는 필수입니다.")
        Long productId,

        @NotBlank(message = "질문 내용은 필수입니다.")
        String question
    ) {}

    public record AnswerRequest(
        @NotBlank(message = "답변 내용은 필수입니다.")
        String answer
    ) {}

    public record Response(
            Long id,
            String question,
            String answer,
            String authorName,
            LocalDateTime createdAt,
            LocalDateTime answeredAt
    ) {
        public static Response fromEntity(Qna qna) {
            return new Response(
                    qna.getId(),
                    qna.getQuestion(),
                    qna.getAnswer(),
                    qna.getUser().getUsername(),
                    qna.getCreatedAt(),
                    qna.getAnsweredAt()
            );
        }
    }
}
