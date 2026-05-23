package com.jjsoft.pos.dto.groupbuy;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 *  공동구매 가격 정책 dto
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupbuyPriceStepDto {

    private Long priceStepId;

    private Integer stepQtyFrom;
    private Integer stepQtyTo;

    private Integer salesPrice;
    private BigDecimal  salesRate;
}
