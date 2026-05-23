package com.jjsoft.pos.dto.summary                        ;

import lombok.AllArgsConstructor                        ;
import lombok.Builder                        ;
import lombok.Data                        ;
import lombok.NoArgsConstructor                        ;
import lombok.ToString                        ;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SummaryDataDto {
	
	/** 영업실적 현황  */
	private String salesDate                        ;
	private Integer totalSales                        ;
	private Integer totalMargin                        ;
	private Double  totalMarginRate                        ;
	private Integer visitSales                        ;
	private Integer visitMargin                        ;
	private Double  visitMarginRate                        ;
	private Integer pickupSales                        ;
	private Integer pickupMargin                        ;
	private Double  pickupMarginRate                        ;
	private Integer visitCount                        ;
	private Integer pickupCount                        ;
	private Integer totalCount                        ;
	
	
	/** 상품판매 현황  */
	private String productId                        ;
	private String productNm                        ;
	private String pvarcodeNo                        ;
	private String lotNo                        ;
	private String categoryNm                        ;
	private String storeNm                        ;
	private Integer totalQty                        ;
//	private String totalSales                        ;
//	private String totalMargin                        ;
	private Double marginRate                        ;
	private Double costRate                        ;//원가율
	private Integer paymentCount                        ;
	private Integer currentStock                        ;
	private Integer orgStockQty                        ;
	
	/** 고객별 구매통계  */
	private String userId                        ;
	private String userNm                        ;
	private String locationNm                        ;
//	private String visitCount                        ;
	private Integer reservationCount                        ;
	private Integer totalPurchaseCount                        ;
	private Integer reservationCancelCount                        ;
//	private String visitSales                        ;
	private Integer reservationSales                        ;
	
	
}
