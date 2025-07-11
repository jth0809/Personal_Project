package com.personal.backend.exception;

import com.personal.backend.dto.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// @RestControllerAdvice: 모든 @RestController에서 발생하는 예외를 이 클래스가 처리하도록 합니다.
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * EntityNotFoundException 처리: 리소스를 찾지 못했을 때 (404 Not Found)
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        ErrorResponse response = ErrorResponse.of("NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * IllegalArgumentException 처리: 잘못된 인자 값이 들어왔을 때 (400 Bad Request)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse response = ErrorResponse.of("BAD_REQUEST", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * SecurityException 처리: 접근 권한이 없을 때 (403 Forbidden)
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponse> handleSecurityException(SecurityException ex) {
        ErrorResponse response = ErrorResponse.of("FORBIDDEN", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * 그 외 모든 예외 처리: 예측하지 못한 서버 내부 오류 (500 Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        // 실제 운영 환경에서는 로그를 남기는 것이 매우 중요합니다.
        // log.error("Internal Server Error", ex);
        ErrorResponse response = ErrorResponse.of("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
