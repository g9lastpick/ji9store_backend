package com.jjsoft.pos.dto.mobile;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 「지구 한바퀴」 - 사용자 구매내역을 상품명 단위로 집계한 원시 행.
 * 상품명 안의 "(지역명)" 텍스트로 권역을 판정한다.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductRegionAggDto {

    /** 상품명(product_mst.PRODUCT_NM) */
    private String productNm;

    /** 해당 상품의 구매(판매상세) 건수 */
    private long cnt;

    /** 해당 상품 최초 구매 일시 */
    private LocalDateTime firstDate;
}
