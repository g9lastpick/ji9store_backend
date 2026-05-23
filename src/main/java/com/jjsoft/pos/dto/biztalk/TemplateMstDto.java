package com.jjsoft.pos.dto.biztalk;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📦 비즈톡 템플릿 마스터 클라이언트 DTO
 * - Entity: TemplateMstEntity 기반
 * - 프론트/외부 API 전송 및 조회용
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateMstDto {

    /** 내부 식별자 */
    private Long id;

    /** 매장 ID */
    private Long storeId;

    /** 지점 ID */
    private Long locationId;

    /** API 서버 이름 (예: biztalk) */
    private String apiSvrName;

    /** SNS 타입 (예: 알림톡, 친구톡 등) */
    private String snsType;

    /** BIZTALK 템플릿 코드 */
    private String tmplCode;

    /** BIZTALK 템플릿 명 */
    private String tmplName;

    /** 강조 표시 타이틀 */
    private String tmplTitle;

    /** 템플릿 내용 */
    private String tmplContent;

    /** 웹 버튼 리스트
     *  예시: AC@채널추가@@WL@예약상품보기@https://g9system.com/mobile/orderList
     */
    private String webBtn;

    /** 사용 여부 */
    private String useYn;

    /** 설명 */
    private String descrition;

    /** 생성자 */
    private String createUser;

    /** 생성일자 */
    private LocalDateTime createDate;

    /** 수정자 */
    private String updateUser;

    /** 수정일자 */
    private LocalDateTime updateDate;
}
