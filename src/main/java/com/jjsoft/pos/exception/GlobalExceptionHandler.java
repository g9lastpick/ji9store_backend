package com.jjsoft.pos.exception;

import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.jjsoft.pos.enums.ResponseCode;
import com.jjsoft.pos.response.ApiResponse;

import jakarta.persistence.EntityNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 사용자 정의 예외 처리 (UserException)
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserException(GlobalException ex) {
    	 ApiResponse<Object> apiResponse = ApiResponse.builder()
                 .success(false)
                 .message(ex.getErrorMessage())
                 .data(null)
                 .build();

         return ResponseEntity
                 .status(HttpStatus.BAD_REQUEST)
                 .body(apiResponse);
    }
    
 // EntityNotFoundException 처리
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleEntityNotFoundException(
            EntityNotFoundException ex
    ) {

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .build();

        return new ResponseEntity<>(
                apiResponse,
                HttpStatus.NOT_FOUND
        );
    }

    /** SQLException 처리 */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ApiResponse<Object>> handleSQLException(
            SQLException ex
    ) {

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .build();

        return new ResponseEntity<>(
                apiResponse,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }


    /** 서버 예외 처리 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(
            Exception ex
    ) {

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                .build();

        return new ResponseEntity<>(
                apiResponse,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    
}

