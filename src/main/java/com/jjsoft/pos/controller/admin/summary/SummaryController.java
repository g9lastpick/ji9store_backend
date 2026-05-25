package com.jjsoft.pos.controller.admin.summary;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjsoft.pos.dto.summary.DailySalesItemExcelDto;
import com.jjsoft.pos.dto.summary.SummaryChartDto;
import com.jjsoft.pos.dto.summary.SummaryDataDto;
import com.jjsoft.pos.dto.summary.SummarySearchCondition;
import com.jjsoft.pos.keycloak.JwtPrincipalUtils;
import com.jjsoft.pos.response.ApiResponse;
import com.jjsoft.pos.service.admin.summary.SummaryService;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * admin 특가 등록 관리
 */
@RestController
@RequestMapping("/api/admin/summary")
@RequiredArgsConstructor
@Log4j2
public class SummaryController {
	
	private final SummaryService summaryService;

	/**
	 * 클라이언트가 보낸 storeId는 무시하고 JWT의 store_id claim으로 강제 덮어쓴다.
	 * Why: 인증된 매장 컨텍스트를 우회한 타 매장 데이터 조회 방지.
	 */
	private void bindStoreIdFromJwt(SummarySearchCondition condition) {
		condition.setStoreId(JwtPrincipalUtils.requireStoreId());
	}

	/** sales 타입별 누적 집계 조회 - 첫 페이지 진입시만 조회 */
	@GetMapping("/getTotalAggregationChart")
	public ResponseEntity<ApiResponse<Object>> getTotalAggregationChart(@ModelAttribute SummarySearchCondition condition) {
		bindStoreIdFromJwt(condition);
		List<SummaryChartDto> list = summaryService.getTotalAggregationChart(condition);
	    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(list));
	}

	/** 월별 누적집계 조회  */
	@GetMapping("/getTotalMonthAggregationChart")
	public ResponseEntity<ApiResponse<Object>> getTotalMonthAggregationChart(@ModelAttribute SummarySearchCondition condition) {
		bindStoreIdFromJwt(condition);
		List<SummaryChartDto> list = summaryService.getTotalMonthAggregationChart(condition);
	    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(list));
	}

	/** 기간별 매출실적 조회 - 상단 검색 조건에 의해 변경됨. */
	@GetMapping("/getTotalSalesSumChart")
	public ResponseEntity<ApiResponse<Object>> getTotalSalesSumChart(@ModelAttribute SummarySearchCondition condition) {
		bindStoreIdFromJwt(condition);
		List<SummaryChartDto> list = summaryService.getTotalSalesSumChart(condition);
	    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(list));
	}

	/** 기간별 상품실적 30개 상품- 상단 검색 조건에 의해 변경됨.  */
	@GetMapping("/getProductTotalSalesSumChart")
	public ResponseEntity<ApiResponse<Object>> getProductTotalSalesSumChart(@ModelAttribute SummarySearchCondition condition) {
		bindStoreIdFromJwt(condition);
		List<SummaryChartDto> list = summaryService.getProductTotalSalesSumChart(condition);
	    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(list));
	}

	/** 영업실적 현황 그리드 데이터 */
	@GetMapping("/getTotalSales")
	public ResponseEntity<ApiResponse<Object>> getTotalSales(@ModelAttribute SummarySearchCondition condition) {
		bindStoreIdFromJwt(condition);
		List<SummaryDataDto> list = summaryService.getTotalSales(condition);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(list));
	}
	/** 상품별 판매 현황 그리드 데이터 */
	@GetMapping("/getTotalProductSales")
	public ResponseEntity<ApiResponse<Object>> getTotalProductSales(@ModelAttribute SummarySearchCondition condition) {
		bindStoreIdFromJwt(condition);
		List<SummaryDataDto> list = summaryService.getTotalProductSales(condition);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(list));
	}
	/** 고객별 구매 현황 그리드 데이터 */
	@GetMapping("/getCustomerSales")
	public ResponseEntity<ApiResponse<Object>> getCustomerSales(@ModelAttribute SummarySearchCondition condition) {
		bindStoreIdFromJwt(condition);
		List<SummaryDataDto> list = summaryService.getCustomerSales(condition);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(list));
	}
	
	
	
    
    
    /** 엑셀 업로드 데이터 조회 일자별  */
	@GetMapping("/excelData/{day}")
	public ResponseEntity<ApiResponse<Object>> getExcelDataList(@PathVariable("day") String day) {
		List<DailySalesItemExcelDto> list = summaryService.getExcelDataList(day);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(list));
	}
	
	/** Data Base 데이터 조회 일자별  */
	@GetMapping("/dbData/{day}")
	public ResponseEntity<ApiResponse<Object>> getDBDataList(@PathVariable("day") String day) {
		List<DailySalesItemExcelDto> list = summaryService.getDBDataList(day);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(list));
	}
	
	
	
	/**
     * 📥 일별 매출 단위 엑셀 업로드 API 및 데이터 sync - sales data 생성
     */
    @PostMapping("/exceluploadWithDataSync")
    public ResponseEntity<ApiResponse<Object>> exceluploadWithDataSync(@RequestBody List<DailySalesItemExcelDto> dtoList) {
        String createUser = "excelUploader"; // 로그인 유저 or 시스템 계정
        summaryService.saveOrUpdateFromExcel(dtoList, createUser);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(null));
    }
	
    
    @PostMapping("/exceluploadWithVarcodeCheck")
    public ResponseEntity<ApiResponse<Object>> exceluploadWithVarcodeCheck(
    		@RequestBody VarcodeRequestDto request) {
    	List<Map<String, Object>> result = summaryService.checkVarcodeList(request.getVarcodeList());
    	
    	return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(result));
    }
    
   
    @Data
    public static class VarcodeRequestDto {

        private List<String> varcodeList;

    }
}


