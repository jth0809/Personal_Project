package com.personal.backend.dto;

import java.util.Objects;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

public class PageableDto {
    
    public record PageableRequest(
        @Parameter(description = "페이지 번호 (0..N)", schema = @Schema(type = "integer", defaultValue = "0"))
        Integer page,

        @Parameter(description = "페이지 크기", schema = @Schema(type = "integer", defaultValue = "10"))
        Integer size,

        @Parameter(description = "정렬 기준 필드 (예: 'id')", schema = @Schema(type = "string"))
        String sortBy,

        @Parameter(description = "정렬 순서 (ASC 또는 DESC)", schema = @Schema(type = "string", defaultValue = "DESC", allowableValues = {"ASC", "DESC"}))
        String sortOrder
    ) {
        /**
         * 이 DTO를 Spring Data의 Pageable 객체로 변환합니다.
         * @return Pageable 객체
         */
        public Pageable toPageable() {
            int pageNumber = Objects.nonNull(this.page) && this.page >= 0 ? this.page : 0;
            int pageSize = Objects.nonNull(this.size) && this.size > 0 ? this.size : 10;

            if (Objects.nonNull(this.sortBy) && !this.sortBy.isBlank()) {
                Sort.Direction direction = "ASC".equalsIgnoreCase(this.sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;
                return PageRequest.of(pageNumber, pageSize, Sort.by(direction, this.sortBy));
            }

            // sortBy 파라미터가 없으면 기본 정렬(id, DESC)을 적용합니다.
            return PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        }
    }

}
