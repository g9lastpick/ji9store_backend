package com.jjsoft.pos.controller.admin.groupbuy;

import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjsoft.pos.dto.groupbuy.GroupbuyDetailResponseDto;
import com.jjsoft.pos.dto.groupbuy.GroupbuyJoinRequestDto;
import com.jjsoft.pos.dto.groupbuy.GroupbuyRequestDto;
import com.jjsoft.pos.dto.groupbuy.GroupbuyResponseDto;
import com.jjsoft.pos.dto.groupbuy.GroupbuySearchRequestDto;
import com.jjsoft.pos.response.ApiResponse;
import com.jjsoft.pos.service.admin.groupbuy.GroupbuyAdminService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/** 공동구매 관리자 페이지 */
@RestController
@RequestMapping("/api/admin/groupbuy")
@RequiredArgsConstructor
@Log4j2
public class GroupbuyController {

	/**  공통 필수사항 : store , location 필수 */
	
	/** 공동구매 등록 */
	/** 공동구매 수정 */
	/** 공동구매 삭제 */
	/** 공동구매 조회 */
	/** 공동구매 예약 조회 */
	/** 공동구매 예약 완료처리 : pos완 연동하여 결제 호출 - admin page에서 pos 결제창 호출
	 * ******* 중요 !! : 결제창 호출시 pos에서 수량 및 금액 조정 불가하게 셋팅해야함 */
	
	
//	private final GroupbuyAdminService groupbuyAdminService;

	@GetMapping("/healcheck")
	public String healcheck() {
		
		return "healcheck";
	}
	
	/* =========================================================
     * 공동구매 목록 조회
     * storeId / locationId 필수 
     * ========================================================= */
	@GetMapping
	public ResponseEntity<ApiResponse<Object>>  getGroupbuyList( @Validated  @ModelAttribute GroupbuySearchRequestDto  requestDto) {
//	    log.info("[ADMIN][GROUPBUY][LIST] requestDto={}", requestDto);
//
//	    List<GroupbuyResponseDto> result = groupbuyAdminService.getGroupbuyList(requestDto);
//	    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(result));
		return null;
	}
	
    /* =========================================================
     * 공동구매 단건 조회 
     * ========================================================= */
    @GetMapping("/{groupbuyId}")
    public ResponseEntity<ApiResponse<Object>> getGroupbuyDetail(@PathVariable("groupbuyId") Long groupbuyId) {
//    	log.info("[ADMIN][GROUPBUY][DETAIL] groupbuyId={}", groupbuyId);
//    	GroupbuyDetailResponseDto result = groupbuyAdminService.getGroupbuyDetail(groupbuyId);
//    	return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(result));
    	return null;
    }
	
    /* =========================================================
     * 공동구매 등록
     * ========================================================= */
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createGroupbuy(@RequestBody GroupbuyRequestDto requestDto) {
        
//    	log.info("[ADMIN][GROUPBUY][CREATE] request={}", requestDto);        
//        Long groupbuyId = groupbuyAdminService.createGroupbuy(requestDto);
//        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(groupbuyId));
    	return null;
    }

    /* =========================================================
     * 공동구매 수정
     * ========================================================= */
    @PutMapping("/{groupbuyId}")
    public ResponseEntity<ApiResponse<Object>> updateGroupbuy(@PathVariable("groupbuyId") Long groupbuyId,@RequestBody GroupbuyRequestDto requestDto) {
        
//    	log.info("[ADMIN][GROUPBUY][UPDATE] groupbuyId={}, request={}", groupbuyId, requestDto);
//        groupbuyAdminService.updateGroupbuy(groupbuyId, requestDto);
//        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(groupbuyId));
    	return null;
    }

    /* =========================================================
     * 공동구매 삭제 (상태 CANCEL 처리)
     * ========================================================= */
    @DeleteMapping("/{groupbuyId}")
    public ResponseEntity<ApiResponse<Object>> deleteGroupbuy( @PathVariable("groupbuyId") Long groupbuyId) {
        
//    	log.info("[ADMIN][GROUPBUY][DELETE] groupbuyId={}", groupbuyId);
//        groupbuyAdminService.cancelGroupbuy(groupbuyId);
//        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok("Success"));
    	return null;
    }


    /* =========================================================
     * 공동구매 참여 / 수정 / 취소
     * ========================================================= */
    @PostMapping("/join")
    public ResponseEntity<ApiResponse<Object>> enterGroupbuy(
            @RequestBody GroupbuyJoinRequestDto requestDto
    ) {

//        log.info("[ADMIN][GROUPBUY][JOIN] request={}", requestDto);
//
//        groupbuyAdminService.enterGroupbuy(
//                requestDto.getGroupbuyId(),
//                requestDto.getUserId(),
//                requestDto.getJoinQty(),
//                requestDto.getRequestStatus()
//        );
//
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(ApiResponse.ok("Success"));
    	return null;
    }
}
