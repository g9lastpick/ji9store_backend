package com.jjsoft.pos.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 비즈톡 알림톡 전송 로그 Entity
 * biztalk_send_log 테이블 매핑
 */
@Entity
@Table(name = "biztalk_send_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Comment("비즈톡 알림톡 전송 로그")
public class BiztalkSendLogEntity {

    /** 내부 식별자 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @Comment("전송 로그 ID")
    private Long id;
    
    @Column(name = "STORE_ID")
    @Comment("STORE ID")
    private Long storeId;
    
    @Column(name = "LOCATION_ID")
    @Comment("지점 ID")
    private Long locationId;

    /** 메시지 고유 UUID (식별자) */
    @Column(name = "MSG_IDX", nullable = false, unique = true, length = 400)
    @Comment("식별자 UUID 조합 으로 발송 , 메시지 전송 고유키")
    private String msgIdx;

    /** 비즈톡 내부 식별자 */
    @Column(name = "BIZTALK_ID", length = 400)
    @Comment("BIZ TALK 식별자")
    private String biztalkId;

    /** 비즈톡 전송 결과 코드 */
    @Column(name = "RES_CODE", length = 100)
    @Comment("biztalk에서 user로 메시지 전송 결과 코드값")
    private String resCode;

    /** 수신 대상 유저 ID */
    @Column(name = "USER_ID")
    @Comment("대상 유저 ID")
    private String userId;

    /** 수신자 전화번호 */
    @Column(name = "PHONE_NO", nullable = false, length = 20)
    @Comment("수신자 전화번호")
    private String phoneNo;

    /** 비즈톡 템플릿 코드 */
    @Column(name = "TMPL_CODE", nullable = false, length = 50)
    @Comment("비즈톡 템플릿 코드")
    private String tmplCode;
    
    /** 비즈톡 템플릿 코드 */
    @Column(name = "REQ_API_URI", length = 100)
    @Comment("요청 api uri")
    private String reqApiUri;

    /** 전송 메시지 내용 */
    @Column(name = "MESSAGE_CONTENT", length = 1000)
    @Comment("발송 메시지 내용")
    private String messageContent;

    /** 비즈톡 요청 JSON 데이터 */
    @Lob
    @Column(name = "REQUEST_JSON", columnDefinition = "TEXT")
    @Comment("attach에 들어있는 내용")
    private String requestJson;

    /** 비즈톡 응답 JSON 데이터 */
    @Lob
    @Column(name = "RESPONSE_JSON", columnDefinition = "TEXT")
    @Comment("응답 JSON")
    private String responseJson;

    /** 비즈톡 서버 전송 상태 */
    @Column(name = "SEND_STATUS", length = 20)
    @Comment("BIZ TALK전송 상태 (SUCCESS / FAIL)")
    private String sendStatus;

    /** 메시지 전송 상태 */
    @Column(name = "SEND_MSG_STATUS", length = 20)
    @Comment("MSG 전송 상태 (SUCCESS / FAIL)")
    private String sendMsgStatus;

    /** 비즈톡 서버 전송 일시 */
    @CreationTimestamp
    @Column(name = "SEND_DATE")
    @Comment("전송일시")
    private LocalDateTime sendDate;

    /** 메시지 전송 완료 일시 */
    @CreationTimestamp
    @Column(name = "SEND_MSG_DATE")
    @Comment("메시지 전송 완료일시")
    private LocalDateTime sendMsgDate;

    /** 생성자 */
    @Column(name = "CREATE_USER", length = 50)
    @Comment("생성자")
    private String createUser;

    /** 생성일자 */
    @CreationTimestamp
    @Column(name = "CREATE_DATE")
    @Comment("생성일자")
    private LocalDateTime createDate;
    /** 생성자 */
    @Column(name = "UPDATE_USER", length = 50)
    @Comment("수정자")
    private String updateUser;
    
    /** 생성일자 */
    @CreationTimestamp
    @Column(name = "UPDATE_DATE")
    @Comment("수정일자")
    private LocalDateTime updateDate;
}
