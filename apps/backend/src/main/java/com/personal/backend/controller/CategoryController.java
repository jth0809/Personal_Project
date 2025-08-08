package com.personal.backend.controller;

import com.personal.backend.dto.CategoryDto;
import com.personal.backend.service.ProductService; // 또는 CategoryService
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "카테고리 API", description = "카테고리 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final ProductService productService; // CategoryService를 만들었다면 그것을 주입

    @Operation(summary = "모든 카테고리 목록 조회", description = "상품 생성/수정 시 사용할 카테고리 목록을 제공합니다.")
    @GetMapping
    public ResponseEntity<List<CategoryDto.Response>> getAllCategories() {
        List<CategoryDto.Response> categories = productService.findAllCategories();
        return ResponseEntity.ok(categories);
    }
}