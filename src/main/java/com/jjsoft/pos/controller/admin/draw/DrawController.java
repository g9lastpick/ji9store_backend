package com.jjsoft.pos.controller.admin.draw;

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

import com.jjsoft.pos.dto.draw.DrawDetailResponseDto;
import com.jjsoft.pos.dto.draw.DrawEntryRequestDto;
import com.jjsoft.pos.dto.draw.DrawRequestDto;
import com.jjsoft.pos.dto.draw.DrawResponseDto;
import com.jjsoft.pos.dto.draw.DrawSearchRequestDto;
import com.jjsoft.pos.response.ApiResponse;
import com.jjsoft.pos.service.admin.draw.DrawAdminService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/** 드로우 이벤트 관리자 페이지 */
//@RestController
@RequestMapping("/api/admin/draw")
@RequiredArgsConstructor
@Log4j2
public class DrawController {
	
//	private final DrawAdminService drawAdminService;

	@GetMapping("/healcheck")
	public String healcheck() {
		
		return "healcheck";
	}
	
	/* =========================================================
     * 드로우 목록 조회
     * storeId / locationId 필수 ResponseEntity<List<DrawResponseDto>>
     * ========================================================= */
	@GetMapping
	public ResponseEntity<ApiResponse<Object>> getDrawList( @Validated  @ModelAttribute DrawSearchRequestDto  requestDto) {
	    log.info("[ADMIN][Draw][LIST] requestDto={}", requestDto);
//	    List<DrawResponseDto> result = drawAdminService.getDrawList(requestDto);
//	    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(result));
	    return null;
	}
	
    /* =========================================================
     * 드로우 단건 조회 ResponseEntity<DrawDetailResponseDto>
     * ========================================================= */
    @GetMapping("/{DrawId}")
    public ResponseEntity<ApiResponse<Object>> getDrawDetail(@PathVariable("DrawId") Long DrawId) {
    	log.info("[ADMIN][Draw][DETAIL] DrawId={}", DrawId);
//    	DrawDetailResponseDto result = drawAdminService.getDrawDDetail(DrawId);
//    	return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(result));
    	return null;
    }
	
    /* =========================================================
     * 드로우 등록
     * ========================================================= */
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createDraw(@RequestBody DrawRequestDto requestDto) {
        
//    	log.info("[ADMIN][Draw][CREATE] request={}", requestDto);        
//        Long result = drawAdminService.createDraw(requestDto);
//        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(result));
    	return null;
    }

    /* =========================================================
     * 드로우 수정
     * ========================================================= */
    @PutMapping("/{DrawId}")
    public ResponseEntity<ApiResponse<Object>> updateDraw(@PathVariable("DrawId") Long DrawId,@RequestBody DrawRequestDto requestDto) {
        
//    	log.info("[ADMIN][Draw][UPDATE] DrawId={}, request={}", DrawId, requestDto);
//    	drawAdminService.updateDraw(DrawId, requestDto);
//    	return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok("Success"));
    	return null;
    }

    /** =========================================================
     * 드로우 삭제 (상태 CANCEL 처리)
     * ========================================================= */
    @DeleteMapping("/{drawId}")
    public ResponseEntity<ApiResponse<Object>> deleteDraw( @PathVariable("drawId") Long drawId) {
        
//    	log.info("[ADMIN][Draw][CANCEL] drawId={}", drawId);
//    	drawAdminService.cancelDraw(drawId);
//    	return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok("Success"));
    	return null;
    }
    
    
    /** 드로우 참여 */
    @PostMapping("/entry")
    public ResponseEntity<ApiResponse<Object>> entryDraw( @RequestBody DrawEntryRequestDto requestDto) {
        
//    	log.info(
//    	        "[ADMIN][DRAW][ENTRY] drawId={} userId={} status={}",
//    	        requestDto.getDrawId(),
//    	        requestDto.getUserId(),
//    	        requestDto.getEntryStatus()
//    	    );
//    	drawAdminService.enterDraw(
//                requestDto.getDrawId(),
//                requestDto.getUserId(),
//                requestDto.getEntryStatus()
//        );
//    	return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok("Success"));
    	return null;
    }

    



}
