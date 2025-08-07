package com.personal.backend.controller;

import com.personal.backend.dto.ImageDto;
import com.personal.backend.config.oci.OciUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "이미지 API", description = "이미지 업로드 URL 생성 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
@Profile("!test")
public class ImageController {

    private final OciUploadService ociUploadService;

    @Operation(summary = "단일 이미지 업로드 URL 생성", description = "OCI Object Storage에 직접 업로드할 수 있는 임시 URL을 발급합니다.")
    @PostMapping("/generate-upload-url")
    public ResponseEntity<ImageDto.GenerateUploadUrlResponse> generateUploadUrl(
            @RequestBody ImageDto.GenerateUploadUrlRequest request
    ) {
        ImageDto.GenerateUploadUrlResponse response = ociUploadService.generatePreAuthenticatedUploadUrl(request);
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "다중 이미지 업로드 URL 생성", description = "OCI Object Storage에 직접 업로드할 수 있는 임시 URL을 발급합니다.")
    @PostMapping("/generate-upload-urls")
    public ResponseEntity<List<ImageDto.UploadInfoResponse>> generateUploadUrls(
            @RequestBody ImageDto.GenerateUploadUrlsRequest request
    ) {
        List<ImageDto.UploadInfoResponse> responses = ociUploadService.generatePreAuthenticatedUploadUrls(request);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "이미지 삭제", description = "OCI Object Storage에서 이미지를 삭제하고 DB에서 이미지를 삭제합니다.")
    @DeleteMapping("\"/{objectName}/products/{productId}\"")
    public ResponseEntity<Void> deleteImage(
        @PathVariable Long productId,
        @PathVariable String objectName,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userEmail = userDetails.getUsername();
        ociUploadService.deleteImage(productId,objectName,userEmail);
        return ResponseEntity.ok().build();
    }

}