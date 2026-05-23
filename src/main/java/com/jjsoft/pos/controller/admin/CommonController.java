package com.jjsoft.pos.controller.admin;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jjsoft.pos.dto.common.CategoryDto;
import com.jjsoft.pos.dto.common.CodeValueDto;
import com.jjsoft.pos.mapper.CommonMapper;
import com.jjsoft.pos.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 *  공통 코드 등 관리 
 */
@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
@Log4j2
public class CommonController {

	private final CommonMapper commonMapper;
	
	@GetMapping("/selectPartners")
	public ResponseEntity<List<CodeValueDto>>  selectPartners(@RequestParam("storeId") Long storeId) {
	    return ResponseEntity.ok(commonMapper.selectPartners(storeId));
	}
	
	@GetMapping("/selectCategorys")
	public ResponseEntity<List<CodeValueDto>> selectCategorys(@RequestParam("storeId") Long storeId) {
		 return ResponseEntity.ok(commonMapper.selectCategorys(storeId));
	}
	@GetMapping("/selectLocations")
	public ResponseEntity<List<CodeValueDto>> selectLocations(@RequestParam("storeId") Long storeId) {
		return ResponseEntity.ok(commonMapper.selectLocations(storeId));
	}
	@GetMapping("/selectStatus")
	public ResponseEntity<List<CodeValueDto>> selectStatus(@RequestParam("storeId") Long storeId) {
		return ResponseEntity.ok(commonMapper.selectStatus(storeId));
	}
	@GetMapping("/selectSalesStatus")
	public ResponseEntity<List<CodeValueDto>> selectSalesStatus(@RequestParam("storeId") Long storeId) {
		return ResponseEntity.ok(commonMapper.selectSalesStatus(storeId));
	}
	@GetMapping("/selectSalesType")
	public ResponseEntity<List<CodeValueDto>> selectSalesType(@RequestParam("storeId") Long storeId) {
		return ResponseEntity.ok(commonMapper.selectSalesType(storeId));
	}
	@GetMapping("/selectPaymentType")
	public ResponseEntity<List<CodeValueDto>> selectPaymentType(@RequestParam("storeId") Long storeId) {
		return ResponseEntity.ok(commonMapper.selectPaymentType(storeId));
	}
	
	@GetMapping("/selectProgressList")
	public ResponseEntity<List<CodeValueDto>> selectProgressList(@RequestParam("storeId") Long storeId) {
		return ResponseEntity.ok(commonMapper.selectProgressList(storeId));
	}
	
	@GetMapping("/getSpecialKeyList/{startDate}")
	public ResponseEntity<List<CodeValueDto>> getSpecialKeyList(@PathVariable("startDate") String startDate) {
		return ResponseEntity.ok(commonMapper.selectSpecialKeyList(startDate));
	}
	@GetMapping("/selectReservationList/{storeId}")
	public ResponseEntity<List<CodeValueDto>> selectReservationList(@PathVariable("storeId") Long storeId) {
		return ResponseEntity.ok(commonMapper.selectReservationList(storeId));
	}
	
	
	 

//	@PostMapping("/runSql")
	public ResponseEntity<ApiResponse<Object>> runSql(@RequestBody Map<String, String> body) {
        String sql = body.get("sql");
        
        // ⚠️ 보안 체크: SELECT 문만 실행
        String upper = sql.trim().toUpperCase();
        if (!upper.startsWith("SELECT")) {
            throw new IllegalArgumentException("SELECT 문만 실행 가능합니다.");
        }
        List<Map<String, Object>> result = commonMapper.sqlplay(sql);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(result));
    }
	
	
	@GetMapping("/categoryAll/{storeId}")
	public ResponseEntity<ApiResponse<Object>> selectCategoryAll(@PathVariable("storeId") long storeId) {
		List<CategoryDto> list = commonMapper.selectCategoryAll(storeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(list));
    }

}
