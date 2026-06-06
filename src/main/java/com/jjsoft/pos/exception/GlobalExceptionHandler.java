package com.jjsoft.pos.exception;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.jjsoft.pos.response.ApiResponse;

import jakarta.persistence.EntityNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 사용자 정의 예외 (의도된 메시지만 클라이언트에 전달) */
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserException(GlobalException ex) {
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(false)
                .message(ex.getErrorMessage())
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    /** EntityNotFound - 내부 메시지 비노출 */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.warn("EntityNotFound: {}", ex.getMessage());
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(false)
                .message("요청한 데이터를 찾을 수 없습니다.")
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    /** SQLException - SQL/테이블/쿼리 등 내부정보 비노출 */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ApiResponse<Object>> handleSQLException(SQLException ex) {
        log.error("SQLException", ex);
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(false)
                .message("데이터 처리 중 오류가 발생했습니다.")
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /** 그 외 모든 예외 - 스택/원본 메시지 비노출 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {
        log.error("Unhandled exception", ex);
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(false)
                .message("서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.")
                .data(null)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
