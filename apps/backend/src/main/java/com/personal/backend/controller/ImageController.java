package com.personal.backend.controller;

import com.personal.backend.dto.ImageDto;
import com.personal.backend.config.oci.OciUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "이미지 업로드 URL 생성", description = "OCI Object Storage에 직접 업로드할 수 있는 임시 URL을 발급합니다.")
    @PostMapping("/generate-upload-url")
    public ResponseEntity<ImageDto.GenerateUploadUrlResponse> generateUploadUrl(
            @RequestBody ImageDto.GenerateUploadUrlRequest request
    ) {
        ImageDto.GenerateUploadUrlResponse response = ociUploadService.generatePreAuthenticatedUploadUrl(request);
        return ResponseEntity.ok(response);
    }
}