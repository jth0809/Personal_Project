package com.personal.backend.dto;

import com.personal.backend.domain.Tag;
import io.swagger.v3.oas.annotations.media.Schema;

public class TagDto {

    @Schema(name = "TagResponse", description = "태그 응답 DTO")
    public record Response(
            Long id,
            String name
    ) {
        public static Response fromEntity(Tag tag) {
            return new Response(
                    tag.getId(),
                    tag.getName()
            );
        }
    }
}
