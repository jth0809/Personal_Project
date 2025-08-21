package com.personal.backend.controller;

import com.personal.backend.domain.Tag;
import com.personal.backend.repository.TagRepository;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@io.swagger.v3.oas.annotations.tags.Tag(name = "태그 API", description = "태그 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
public class TagController {

    private final TagRepository tagRepository;

    @Operation(summary = "모든 태그 조회", description = "모든 태그 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<Tag>> getAllTags() {
        return ResponseEntity.ok(tagRepository.findAll());
    }
}