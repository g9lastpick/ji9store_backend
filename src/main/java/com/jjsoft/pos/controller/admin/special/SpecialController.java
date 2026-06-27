package com.jjsoft.pos.controller.admin.special;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jjsoft.pos.dto.special.ReservationItemDto;
import com.jjsoft.pos.dto.special.SpecialCalendarEventDto;
import com.jjsoft.pos.dto.special.SpecialColumnsDto;
import com.jjsoft.pos.dto.special.SpecialDtlDto;
import com.jjsoft.pos.dto.special.SpecialMstDto;
import com.jjsoft.pos.dto.special.SpecialReservationDto;
import com.jjsoft.pos.dto.special.UserPickupItemDto;
import com.jjsoft.pos.dto.special.PickupBatchRequestDto;
import com.jjsoft.pos.dto.special.PickupUserDto;
import com.jjsoft.pos.dto.special.condition.SpecialSearchCondition;
import com.jjsoft.pos.response.ApiResponse;
import com.jjsoft.pos.service.admin.special.SpecialService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * admin 특가 등록 관리
 */
@RestController
@RequestMapping("/api/admin/special")
@RequiredArgsConstructor
@Log4j2
public class SpecialController {
	
	private final SpecialService specialService;
	private final com.jjsoft.pos.security.StoreAccessGuard storeAccessGuard;

	/** 특가 저장된 데이터 => calendar data list로  조회 */
	@GetMapping("/getSpecialsForCalendar")
	public ResponseEntity<ApiResponse<Object>> getSpecialsForCalendar(@RequestParam("start") String startDate,@RequestParam("end") String  endDate ,@RequestParam("locationId") int locationId) {
	    List<SpecialCalendarEventDto>  list =  specialService.selectSpecialsForCalendar(startDate, endDate , locationId);
	    
	    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(list));
	}
	/** 특가 저장된 데이터 => 특가상품 정보 조회 */
	@GetMapping("/getSpecialProductList/{specialId}")
	public ResponseEntity<ApiResponse<Object>> getSpecialProductList(@PathVariable("specialId") Long specialId) {
	    List<SpecialDtlDto> list = specialService.getSpecialProductList(specialId);
	    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(list));
	}

	/** 특가 노출 소비기한 선택용: 가용재고(FIFO 잔여)≥1 lot의 소비기한 목록 조회 */
	@GetMapping("/getAvailableLots")
	public ResponseEntity<ApiResponse<Object>> getAvailableLots(@RequestParam("productId") Long productId, @RequestParam("locationId") Long locationId) {
	    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(specialService.getAvailableLots(productId, locationId)));
	}

	/** 특가상품 저장 */
	@PostMapping("/specialSave")
	public ResponseEntity<ApiResponse<Object>> specialSave(@RequestBody SpecialMstDto dto, @AuthenticationPrincipal Jwt jwt) {
		String userId = "";
    	if (jwt != null) {
			userId = jwt.getClaim("email"); // email preferred_username
	    }
	    try {
	        Long specialId = specialService.saveOrUpdate(dto,userId);
	        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(specialId));
	    } catch (IllegalStateException | IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.fail(e.getMessage()));
	    }
	}
	/** 특가 등록 부분저장 - 
	 * 특가 진행중일 경우 픽업시간과 특가명  상태 변경가능  */
	@PostMapping("/specialSavePartial")
	public ResponseEntity<ApiResponse<Object>> specialSavePartial(@RequestBody SpecialMstDto dto, @AuthenticationPrincipal Jwt jwt) {
		String userId = "";
		if (jwt != null) {
			userId = jwt.getClaim("email"); // email preferred_username
		}
		Long specialId = specialService.saveOrUpdatePartial(dto,userId);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(specialId));
	}
	
	
	@DeleteMapping("/delete/{specialId}")
	public ResponseEntity<ApiResponse<Object>> deleteSpecial(@PathVariable("specialId") Long specialId) {
		try {
	        specialService.deleteSpecial(specialId);
	        return ResponseEntity.ok(ApiResponse.ok("삭제 완료"));
	    } catch (IllegalStateException e) {
	        return ResponseEntity
	            .badRequest()
	            .body(ApiResponse.fail("삭제 실패: " + e.getMessage()));
	    }
	}
	
	
	/**
     * 특가 예약 조회
     */
    @GetMapping("/getReservatioList")
    public ResponseEntity<ApiResponse<Object>> getReservatioList(@ModelAttribute SpecialSearchCondition condition) {
    	condition.setStoreId(storeAccessGuard.resolveStoreId(condition.getStoreId()));
    	List<ReservationItemDto> list = specialService.getReservatioList(condition);
    	
    	 return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(list));
    }
	
	
	
    
    
    
    /** 특가 동적 컬럼 조회  */
    @GetMapping("/getSpecialColumns")
    public ResponseEntity<ApiResponse<Object>> getSpecialColumns(@ModelAttribute SpecialSearchCondition condition) {
        condition.setStoreId(storeAccessGuard.resolveStoreId(condition.getStoreId()));
        log.info("getSpecialColumns condition = {}" , condition.toString());
        List<SpecialColumnsDto> list = specialService.getSpecialColumns(condition);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(list));
    }
    
    
    /** 특가 등록화면에서 예약 저장  */
	@PostMapping("/reservationListSave")
	public ResponseEntity<ApiResponse<Object>> reservationListSave(@RequestBody List<ReservationItemDto> list, @AuthenticationPrincipal Jwt jwt) {
		String userId = "";
    	if (jwt != null) {
			userId = jwt.getClaim("email"); // email preferred_username
	    }
	    boolean flag = specialService.reservationListSave(list , userId);
	    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(flag));
	}
    
	/** 예약  삭제 */
	@PostMapping("/reservationDelete")
	public ResponseEntity<ApiResponse<Object>> reservationDelete(@RequestBody ReservationItemDto row) {
		boolean flag = specialService.reservationDelete(row);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(flag));
	}
	
    
	
	
	
	/** 특가 예약 저장  여러건  */
	@PostMapping("/completeReservationList")
	public ResponseEntity<ApiResponse<Object>> completeReservationList(@RequestBody List<ReservationItemDto> list , @AuthenticationPrincipal Jwt jwt) {
		String userId = "";
    	if (jwt != null) {
			userId = jwt.getClaim("email"); // email preferred_username
	    }
		boolean flag = specialService.completeReservationList(list,userId);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(flag));
	}
    
	/** 픽업 예약 관리 - 현재 픽업 가능한 예약이 있는 고객 목록 (type=SPECIAL|GROUPBUY) */
	@GetMapping("/getPickupUsers")
	public ResponseEntity<ApiResponse<Object>> getPickupUsers(
			@RequestParam("type") String type,
			@RequestParam("storeId") Long storeId,
			@RequestParam("locationId") Long locationId) {
		storeId = storeAccessGuard.resolveStoreId(storeId);
		List<PickupUserDto> list = specialService.getPickupUsers(type, storeId, locationId);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(list));
	}

	/** 통합 픽업 항목 조회 (특가 + 공동구매) */
	@GetMapping("/getUserPickupItems")
	public ResponseEntity<ApiResponse<Object>> getUserPickupItems(
			@RequestParam("userId") String userId,
			@RequestParam("storeId") Long storeId,
			@RequestParam("locationId") Long locationId) {
		storeId = storeAccessGuard.resolveStoreId(storeId);
		List<UserPickupItemDto> list = specialService.getUserPickupItems(userId, storeId, locationId);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(list));
	}

	/** 통합 픽업 일괄 완료 (특가 + 공동구매) */
	@PostMapping("/completePickupBatch")
	public ResponseEntity<ApiResponse<Object>> completePickupBatch(@RequestBody PickupBatchRequestDto req, @AuthenticationPrincipal Jwt jwt) {
		String userId = "";
		if (jwt != null) {
			userId = jwt.getClaim("email");
		}
		boolean flag = specialService.completePickupBatch(req, userId);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(flag));
	}

	/** 특가 예약 저장 단건  */
	@PostMapping("/completeReservation")
	public ResponseEntity<ApiResponse<Object>> completeReservation(@RequestBody ReservationItemDto dto , @AuthenticationPrincipal Jwt jwt) {
		String userId = "";
    	if (jwt != null) {
			userId = jwt.getClaim("email"); // email preferred_username
	    }
	    boolean flag = specialService.completeReservation(dto,userId);
	    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(flag));
	}
    
    
}
