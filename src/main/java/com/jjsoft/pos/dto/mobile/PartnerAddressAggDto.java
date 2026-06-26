package com.jjsoft.pos.dto.mobile;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 「지구 한바퀴」 - 사용자 구매내역을 계열사 주소 단위로 집계한 원시 행.
 * (서비스에서 권역으로 환산하기 전 단계)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PartnerAddressAggDto {

    /** 계열사 주소(store_partner_mst.ADDRESS) */
    private String address;

    /** 해당 주소 계열사 상품의 구매(판매상세) 건수 */
    private long cnt;

    /** 해당 주소에서의 최초 구매 일시 */
    private LocalDateTime firstDate;
}
