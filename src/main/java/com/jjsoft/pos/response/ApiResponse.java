package com.jjsoft.pos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
public class ApiResponse<T> {
    private boolean success; // 상태 코드
    private String message;  // 메시지
    private T data;          // 데이터 (제너릭 타입)

    /** 최초 가입 여부 */
    private Boolean isFirstJoin;

    /** 최초 가입 팝업 메시지 */
    private String firstJoinMessage;

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("success")
                .data(data)
                .build();
    } 
    
    public static <T> ApiResponse<T> ok(T data , boolean isFirstJoin , String firstJoinMessage) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("success")
                .data(data)
                .isFirstJoin(isFirstJoin)
                .firstJoinMessage(firstJoinMessage)
                .build();
    } 

    public static <T> ApiResponse<T> fail(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .build();
    }
   
}
