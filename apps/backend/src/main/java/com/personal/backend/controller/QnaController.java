package com.personal.backend.controller;

import com.personal.backend.dto.PageableDto;
import com.personal.backend.dto.QnaDto;
import com.personal.backend.service.QnaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Q&A API", description = "상품 Q&A 조회, 질문 작성, 답변 작성 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/qna")
public class QnaController {

    private final QnaService qnaService;

    @Operation(summary = "상품별 Q&A 목록 조회", description = "특정 상품에 달린 Q&A 목록을 페이지네이션으로 조회합니다.")
    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<QnaDto.Response>> getQna(
            @PathVariable Long productId,
            @ParameterObject PageableDto.PageableRequest pageableRequest
        ){
        Pageable pageable = pageableRequest.toPageable();
        Page<QnaDto.Response> qnaPage = qnaService.getQnaByProductId(productId, pageable);
        return ResponseEntity.ok(qnaPage);
    }

    @Operation(summary = "질문 작성", description = "상품에 대한 새로운 질문을 작성합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/questions")
    public ResponseEntity<QnaDto.Response> createQuestion(
            @Valid @RequestBody QnaDto.CreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        QnaDto.Response newQna = qnaService.createQuestion(userEmail, request);
        return ResponseEntity.ok(newQna);
    }

    @Operation(summary = "답변 작성 (관리자 전용)", description = "Q&A에 대한 답변을 작성합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{qnaId}/answers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QnaDto.Response> addAnswer(
            @PathVariable Long qnaId,
            @Valid @RequestBody QnaDto.AnswerRequest request) {
        QnaDto.Response answeredQna = qnaService.addAnswer(qnaId, request);
        return ResponseEntity.ok(answeredQna);
    }

    @Operation(summary = "Q&A 삭제", description = "자신이 작성한 질문을 삭제합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{qnaId}")
    public ResponseEntity<Void> deleteQna(
            @PathVariable Long qnaId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        qnaService.deleteQna(qnaId, userEmail);
        return ResponseEntity.noContent().build();
    }
}
