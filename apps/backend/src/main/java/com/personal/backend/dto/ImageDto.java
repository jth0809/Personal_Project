package com.personal.backend.dto;

import java.util.List;

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

    public record GenerateUploadUrlsRequest(
            List<String> fileNames
    ) {}

    // 👇 응답 DTO가 파일별 URL 정보를 담는 객체의 '목록'을 반환하도록 수정
    public record UploadInfoResponse(
            String fileName,   // 어떤 파일에 대한 URL 정보인지 식별하기 위함
            String uploadUrl,
            String imageUrl
    ) {}
}