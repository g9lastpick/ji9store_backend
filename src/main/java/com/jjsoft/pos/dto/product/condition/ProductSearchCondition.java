package com.jjsoft.pos.dto.product.condition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductSearchCondition {
	private Long    storeId;
	private Long    locationId;
	private Long    categoryId;
	private Long    partnerId;
	private String  categoryNm;
	private String  productNm;
	private String  pvarcodeNo;
    private String  lotNo;

    private String  receivedDateFrom;
    private String  receivedDateTo;

    private Boolean onlyOutOfStock;
    
    private String masterStatus;//상태
}
