package com.jjsoft.pos.dto.mobile;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** 
 * * 모바일 특가 예약 적용 요청 DTO
 * - 프론트에서 전달한 최종 수량/금액을 담아 보냄
 * - 금액/단가는 서버에서 재조회/재계산 권장 (보안/정합성)
 * */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReservationApplyRequestDto  implements Serializable {
	
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 예약자 외부 식별자 (이메일 등) */
    private String          userId;      

    /** 특가 마스터 ID */
    private Long            specialId;   

    /** 총 예약 수량 (클라 계산 값: Σ (qty+addQty)) */
    private Integer         totalQty;    

    /** 총 예약 금액 (클라 계산 값: Σ (finalQty * unitPrice)) */
    private Integer         totalPrice;  

    /** 예약 마스터 ID (없으면 0 또는 null; 서버에서 (SPECIAL_ID, USER_ID)로 조회/생성) */
    private Long            rsvMstId;    

    /** 아이템별 상세 예약 정보 */
    private List<Item>      reservations;

    /**
     * 상세 아이템
     * - 최종 수량(qty)으로 덮어쓰기 업서트
     * - unitPrice, reservationPrice는 서버에서 재검증/재계산 
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item implements Serializable {

    	private static final long serialVersionUID = 1L;
		
		private Long        rsvMstId;        /** 예약 마스터 ID (없으면 0 또는 null) */
        private Long        specialDtlId;    /** 특가 상세 ID */
        private Integer     oldQty;          /** 기존수량 */
        private Integer     addQty;          /** 신규수량 */
        private Integer     finalQty;        /** 최종 예약 수량 (qty + addQty) */

        private Integer     unitPrice;       /** 단가 (표시/로그용; 서버는 special_dtl.SALES_PRICE로 재조회) */
        private Integer     reservationPrice;/** 행 금액 (표시/로그용; 서버는 qty * unitPrice 로 재계산) */
    }
}