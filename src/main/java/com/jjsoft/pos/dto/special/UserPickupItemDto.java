package com.jjsoft.pos.dto.special;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 고객 단위 통합 픽업 항목 (특가 예약 + 공동구매 예약 통합)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPickupItemDto {

    /** SPECIAL | GROUPBUY */
    private String  type;
    /** 특가=special_rsv_mst_id, 공동구매=groupbuy_join_mst_id */
    private Long    refId;
    /** 특가 예약 상세 ID (상품별 부분 픽업 단위, special_rsv_dtl_id) */
    private Long    rsvDtlId;
    /** 특가=special_id, 공동구매=groupbuy_id */
    private Long    bizId;
    /** 상품 ID */
    private Long    productId;
    /** 특가명 / 공동구매명 */
    private String  title;
    /** 상품명 */
    private String  productNm;
    /** 특가: 상품 종류 수 (1이면 단일) */
    private Integer productCnt;
    /** 예약 수량 */
    private Integer qty;
    /** 확정/판매 단가 */
    private Integer unitPrice;
    /** 예약 금액(합계) */
    private Integer amount;
    /** 픽업 가능 시작일시 (yyyy-MM-dd HH:mm) */
    private String  pickupStartDate;
    /** 픽업 가능 마감일시 (yyyy-MM-dd HH:mm) */
    private String  pickupEndDate;
    /** 현재 픽업 가능 여부 */
    private boolean pickupable;
    /** 픽업 완료(매출확정) 여부 */
    private boolean completed;
    /** 픽업 가능기간 만료 여부 */
    private boolean expired;
    /** 예약 상태 키 */
    private String  status;
}
