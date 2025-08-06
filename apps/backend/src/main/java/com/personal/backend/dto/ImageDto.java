package com.personal.backend.dto;

public class ImageDto {

    /**
     * 클라이언트가 업로드 URL 생성을 요청할 때 사용하는 DTO
     */
    public record GenerateUploadUrlRequest(
            String fileName // 업로드할 파일의 원본 이름
    ) {}

    /**
     * 클라이언트에게 업로드 URL과 최종 이미지 URL을 반환하는 DTO
     */
    public record GenerateUploadUrlResponse(
            String uploadUrl,  // 클라이언트가 파일을 PUT할 임시 업로드 URL
            String imageUrl    // 업로드 완료 후 이미지를 조회할 최종 URL
    ) {}
}