package com.jjsoft.pos.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.jjsoft.pos.dto.sales.SalesDtlDto;
import com.jjsoft.pos.dto.summary.DailySalesItemExcelDto;
import com.jjsoft.pos.dto.summary.ProductStockDto;
import com.jjsoft.pos.dto.summary.SummaryChartDto;
import com.jjsoft.pos.dto.summary.SummaryDataDto;
import com.jjsoft.pos.dto.summary.SummarySearchCondition;
import com.jjsoft.pos.entity.SalesDtlEntity;

@Mapper
public interface SummaryMapper {

	/*차트 데이터 */
	public List<SummaryChartDto> getTotalAggregationChart      (SummarySearchCondition condition);
	public List<SummaryChartDto> getTotalMonthAggregationChart (SummarySearchCondition condition);
	public List<SummaryChartDto> getTotalSalesSumChart         (SummarySearchCondition condition);
	public List<SummaryChartDto> getProductTotalSalesSumChart  (SummarySearchCondition condition);
	
	
	
	/** 그리드 데이터 */
	public List<SummaryDataDto> getTotalSales        (SummarySearchCondition condition);
	public List<SummaryDataDto> getTotalProductSales (SummarySearchCondition condition);
	public List<SummaryDataDto> getCustomerSales     (SummarySearchCondition condition);

	
	
	
	/** excel data List */
	public List<DailySalesItemExcelDto> getExcelDataList (@Param("day")  String day);
	public List<DailySalesItemExcelDto> getDBDataList    (@Param("day")  String day);
	
	
	/**/
	public ProductStockDto getProductStock(
	            @Param("productId") Long productId,
	            @Param("locationId") Long locationId
	    );
	
	
	int getTodaySalesQty(
	        @Param("barcode") String barcode,
	        @Param("locationId") Long locationId,
	        @Param("salesDate") LocalDate salesDate
	);
	
	
	 List<String> selectExistVarcodeList(@Param("varcodeList") List<String> varcodeList);
	 
	 List<SalesDtlEntity> selectSalesDtlForReturn(@Param("salesId")Long salesId , @Param("productId")Long productId);
	 
	 

	 /**
	     * ---------------------------
	     * 기존 sales_dtl 조회
	     * ---------------------------
	     * 할인 분배를 위해 Java에서 처리할 대상 조회
	     */
	 List<SalesDtlDto> getSalesDtlList( @Param("barcode") String barcode, @Param("locationId") Long locationId, @Param("salesDate") LocalDate salesDate);
	 
	 /**
	     * ---------------------------
	     * sales_dtl 할인 MERGE
	     * ---------------------------
	     * Java에서 계산된 discount를 한번에 반영
	     */
	 int updateSalesDtlReservationDiscount(@Param("salesDtlId")  Long salesDtlId , @Param("discountPrice")  int discountPrice);
	 /** 마스터 수량 단가 업데이트  */
	 int updateSalesMstReservationDiscount(@Param("list") List<Long> salesIdList);

}
