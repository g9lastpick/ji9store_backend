package com.jjsoft.pos.dto.biztalk;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiztalkResponseDto {
	private String responseCode;
    private String token;
    private String expireDate;

    
    private String msg;
    
    
    
    private String pk;
    private List<UserResponse> response;
    
    //메지시 전송유무 확인 
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse{
    	private String uid;        // biztalk에서 생성된 메시지 고유 식별자
    	private String msgIdx;     // g9system이 보낸 메시지 고유 식별자
    	private String resultCode; // 결과 코드
    	private String receivedAt;
    	private String requestAt;
    	private String bsid;       // 지구시스템 아이디(g9store)
    	private String sendType;   // 요청타입 k :카카오
    }
}
