package com.jjsoft.pos.dto.special;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 픽업 예약 관리 - 현재 픽업 가능한 예약이 있는 고객 단위 요약
 * (특가: 특가 시작일~픽업종료일 / 공동구매: 픽업 시작일~픽업종료일)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupUserDto {

    /** SPECIAL | GROUPBUY */
    private String  type;
    /** 고객 ID(email) */
    private String  userId;
    /** 고객명 */
    private String  name;
    /** 연락처 */
    private String  phone;
    /** 픽업 가능 예약 건수 */
    private Integer itemCnt;
    /** 예약 수량 합계 */
    private Integer totalQty;
    /** 예약 금액 합계 */
    private Integer totalAmount;
    /** 픽업 가능 시작일시 (가장 빠른) */
    private String  pickupStartDate;
    /** 픽업 가능 마감일시 (가장 늦은) */
    private String  pickupEndDate;
}
