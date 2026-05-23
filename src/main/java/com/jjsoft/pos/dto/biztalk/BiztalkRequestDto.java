package com.jjsoft.pos.dto.biztalk;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 비즈톡 알림톡 요청 DTO
 * 실제 비즈톡 API 호출에 사용되는 필드 정의
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BiztalkRequestDto {

    private Long  storeId;
    private Long  locationId;
	
    private String msgIdx;        /** 메시지 고유 식별자 (UUID 등) */
    private String countryCode;   /** 국가 코드 (예: 82) */
    private String resMethod;     /** 응답 방식 (PUSH 등) */
    private String pk;            /** 비즈톡 내부 식별자 (PUSH 시 전달받는 pk 값) */
    private String userId;        /** 내부 사용자 ID */
    private String appUserId;     /** 앱유저 아이디 recipient또는 appUserId 중 1가지 필수 입력*/
    private String recipient;     /** 수신자 번호 (recipient 필드) */
//    private String phone;         /** 수신자 전화번호 */
    private String tmpltCode;     /** 템플릿 코드 */
    private String title;         /** 메시지 제목 */
    private String message;       /** 메시지 본문 */
    private String plusFriendId;  /** 플러스친구 ID */
    private String senderKey;     /** 발신 프로필 키 */
    private String messageType;   /** 알림톡 유형(기본값 : AT, 이미지 알림톡 : AI) */
    private String reserveDt;     /** 예약 발송 일시 (옵션) */
    private String orgCode;       /** 고객사에서 지정한 부서 구분 코드 */
    private Integer price;        /** 메시지 내 포함된 가격/금액/결제금액 */
    private String currencyType;  /** 메시지 내 포함된 가격/금액/결제금액의 통화단위 (KRW, USD, EUR 등 국제 통화 코드)*/
    private String header;        /** 아이템 리스트 알림톡 상단에 표기할 제목*/
    
    private Attach attach;/** 버튼/첨부 정보 */
    private BiztalkLink link;/** 링크 정보 */


    /* 조건용 객체 */
    private String userName;
    private String tmplCode;
    private String startDate;
    private String endDate;
    // ===============================
    // 내부 하위 객체 클래스 정의
    // ===============================

    /**
     * 버튼 정보 객체 (attach.button[])
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Attach {
        private List<BiztalkButton> button;
    }

    /**
     * 개별 버튼 항목
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class BiztalkButton {
        private String name;         // 버튼명
        private String type;         // 버튼 타입 (AC, WL 등)
        private String url_mobile;    // 모바일 URL
        private String url_pc;        // PC URL
    }

    /**
     * 링크 정보 객체 (link)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class BiztalkLink {
        private String urlMobile;
        private String urlPc;
    }
}
