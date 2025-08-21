package com.personal.backend.controller;

import com.personal.backend.dto.LikeDto;
import com.personal.backend.dto.PageableDto;
import com.personal.backend.dto.ProductDto;
import com.personal.backend.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "찜하기 API", description = "상품 찜하기 추가, 삭제, 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "상품 찜하기", description = "특정 상품을 현재 사용자의 찜 목록에 추가합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<Void> addLike(
            @RequestBody LikeDto.LikeRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        likeService.addLike(userDetails.getUsername(), request.productId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "찜 취소하기", description = "찜 목록에서 특정 상품을 삭제합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeLike(
            @PathVariable Long productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        likeService.removeLike(userDetails.getUsername(), productId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "내 찜 목록 조회", description = "현재 사용자가 찜한 상품 목록을 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<Page<ProductDto.Response>> getMyLikes(
            @AuthenticationPrincipal UserDetails userDetails,
            @ParameterObject PageableDto.PageableRequest pageableRequest) {
        Page<ProductDto.Response> likedProducts = likeService.getLikedProducts(
                userDetails.getUsername(),
                pageableRequest.toPageable()
        );
        return ResponseEntity.ok(likedProducts);
    }
}