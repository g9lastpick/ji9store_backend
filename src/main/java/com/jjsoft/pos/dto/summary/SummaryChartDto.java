package com.jjsoft.pos.dto.summary;

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
public class SummaryChartDto {

	
	/** 전체 누적차트  */
	private String salesType;
	private String totalSales;
	private String totalMargin;
	private String totalMarginRate;
	private String totalPaymentCnt;
	
	
	/** 월별 누적 매출 차트 */
	private String salesMonth;
//	private String totalSales;
//	private String totalMargin;
	private String marginRate;
	private String paymentCount;
	
	/** 월별 일평균 매출 차트 */
	private String avgDailySales  ;//월별 전체일수 기준 일평균 매출
	private String avgDailyMargin ;//월별 일평균 마진
	
	/** 상품별 매출 실적 차트 */
	private String productId;
	private String productNm;
	private String totalQty;
//	private String totalSales;
//	private String totalMargin;
//	private String marginRate;
//	private String paymentCount;
	
	
}
