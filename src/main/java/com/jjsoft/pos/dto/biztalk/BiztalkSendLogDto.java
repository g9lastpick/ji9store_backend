package com.jjsoft.pos.dto.biztalk;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📦 비즈톡 알림톡 전송 로그 클라이언트 DTO
 * - Entity: BiztalkSendLogEntity 기반
 * - 프론트/외부 API 응답용
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BiztalkSendLogDto {

    /** 내부 식별자 */
    private Long id;

    /** 매장 ID */
    private Long storeId;

    /** 지점 ID */
    private Long locationId;

    /** 메시지 고유 UUID */
    private String msgIdx;

    /** 비즈톡 내부 식별자 */
    private String biztalkId;

    /** 비즈톡 전송 결과 코드 */
    private String resCode;

    /** 수신 대상 유저 ID */
    private String userId;

    /** 수신 대상 유저 Name */
    private String userName;
    
    /** 수신자 전화번호 */
    private String phoneNo;

    /** 비즈톡 템플릿 코드 */
    private String tmplCode;
    
    /** 비즈톡 템플릿 코드 */
    private String tmplName;

    /** 요청 API URI */
    private String reqApiUri;

    /** 발송 메시지 내용 */
    private String messageContent;

    /** 요청 JSON */
    private String requestJson;

    /** 응답 JSON */
    private String responseJson;

    /** 비즈톡 서버 전송 상태 (SUCCESS / FAIL) */
    private String sendStatus;

    /** 메시지 전송 상태 (SUCCESS / FAIL) */
    private String sendMsgStatus;

    /** 비즈톡 서버 전송 일시 */
    private LocalDateTime sendDate;

    /** 메시지 전송 완료 일시 */
    private LocalDateTime sendMsgDate;

    /** 생성자 */
    private String createUser;

    /** 생성일자 */
    private LocalDateTime createDate;

    /** 수정자 */
    private String updateUser;

    /** 수정일자 */
    private LocalDateTime updateDate;
    
    /** 기간 조회 조건*/
    private  String startDate;
    private  String endDate;
}
