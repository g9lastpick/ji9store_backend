package com.jjsoft.pos.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jjsoft.pos.dto.common.CategoryDto;
import com.jjsoft.pos.dto.common.CodeValueDto;
import com.jjsoft.pos.mapper.CommonMapper;
import com.jjsoft.pos.repository.StoreMstRepository;
import com.jjsoft.pos.response.ApiResponse;
import com.jjsoft.pos.security.StoreContext;

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
	private final StoreMstRepository storeMstRepository;

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

	/**
	 * 점포선택 드롭다운용 점포 목록.
	 * 본사(SUPER_ADMIN/ADMIN)는 전 점포, 그 외 권한은 본인에게 매핑된 점포만 반환한다.
	 * (StoreContext 는 StoreAccessResolverFilter 가 요청마다 채움)
	 */
	@GetMapping("/selectStores")
	public ResponseEntity<List<CodeValueDto>> selectStores() {
		List<CodeValueDto> list = storeMstRepository.findByUseYnOrderBySortOrderAsc("Y").stream()
				.filter(s -> StoreContext.isSuperAdmin() || StoreContext.canAccess(s.getStoreId()))
				.map(s -> CodeValueDto.builder()
						.value(String.valueOf(s.getStoreId()))
						.label(s.getStoreNm())
						.build())
				.collect(java.util.stream.Collectors.toList());
		return ResponseEntity.ok(list);
	}

	@GetMapping("/categoryAll/{storeId}")
	public ResponseEntity<ApiResponse<Object>> selectCategoryAll(@PathVariable("storeId") long storeId) {
		List<CategoryDto> list = commonMapper.selectCategoryAll(storeId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(list));
    }

}
