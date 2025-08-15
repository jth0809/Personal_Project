package com.personal.backend.dto;

import com.personal.backend.domain.Category;

import io.swagger.v3.oas.annotations.media.Schema;
public class CategoryDto {

    /**
     * 클라이언트에게 카테고리 정보를 응답으로 보낼 때 사용하는 DTO
     */
    @Schema(name = "CategoryResponse", description = "카테고리 응답 DTO")
    public record Response(
            Long id,
            String name
    ) {
        // Category 엔티티를 Response DTO로 변환하는 정적 메소드
        public static Response fromEntity(Category category) {
            return new Response(category.getId(), category.getName());
        }
    }
}