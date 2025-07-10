package com.personal.backend.dto;

// 에러 발생 시, 이 형식에 맞춰 클라이언트에게 응답을 보냅니다.
public record ErrorResponse(
    String errorCode,
    String message
) {
    public static ErrorResponse of(String errorCode, String message) {
        return new ErrorResponse(errorCode, message);
    }
}
