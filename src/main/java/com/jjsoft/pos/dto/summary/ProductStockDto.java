package com.jjsoft.pos.dto.summary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductStockDto {

    private Long productId;
    private String productNm;
    private String barcode;
    private Long locationId;

    private Integer orgStockQty;
    private Integer salesQty;
    private Integer specialQty;
    private Integer availableQty;

}
