package com.jjsoft.pos.controller.mobile;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjsoft.pos.dto.groupbuy.GroupbuyJoinRequestDto;
import com.jjsoft.pos.dto.groupbuy.GroupbuySearchRequestDto;
import com.jjsoft.pos.enums.GroupbuyJoinStatus;
import com.jjsoft.pos.response.ApiResponse;
import com.jjsoft.pos.service.admin.groupbuy.GroupbuyAdminService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * 모바일 공동구매
 * 공통 필수사항 : store , location 필수
 * 보안: 모든 쓰기(예약/취소)의 userId 는 요청 바디가 아니라 JWT(email claim)에서만 취득한다.
 */
@RestController
@RequestMapping("/api/mobile/groupbuy")
@RequiredArgsConstructor
@Log4j2
public class MobileGroupbuyController {

	private final GroupbuyAdminService groupbuyAdminService;

	/** JWT(email)에서 인증 사용자 ID 취득. 바디 userId 는 신뢰하지 않는다. */
	private String resolveUserId(Jwt jwt) {
		return (jwt != null) ? jwt.getClaim("email") : null;
	}

	/** 공동구매 목록 조회 (storeId/locationId 필수) */
	@GetMapping
	public ResponseEntity<ApiResponse<Object>> getGroupbuyList(@Validated @ModelAttribute GroupbuySearchRequestDto requestDto) {
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(
				groupbuyAdminService.getMobileGroupbuyList(requestDto.getStoreId(), requestDto.getLocationId())));
	}

	/** 공동구매 단건 조회 */
	@GetMapping("/{groupbuyId}")
	public ResponseEntity<ApiResponse<Object>> getGroupbuyDetail(@PathVariable("groupbuyId") Long groupbuyId) {
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(groupbuyAdminService.getGroupbuyDetail(groupbuyId)));
	}

	/** 모바일 마이페이지 - 내 공동구매 예약 목록 (userId 는 JWT 에서만 취득) */
	@GetMapping("/my")
	public ResponseEntity<ApiResponse<Object>> myGroupbuyList(@RequestParam(value = "storeId") Long storeId, @AuthenticationPrincipal Jwt jwt) {
		String userId = resolveUserId(jwt);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(groupbuyAdminService.getMyGroupbuyList(userId, storeId)));
	}

	/** 공동구매 예약 (참여) */
	@PostMapping("/join")
	public ResponseEntity<ApiResponse<Object>> join(@RequestBody GroupbuyJoinRequestDto dto, @AuthenticationPrincipal Jwt jwt) {
		String userId = resolveUserId(jwt);
		dto.setUserId(userId);
		log.info("[MOBILE][GROUPBUY][JOIN] groupbuyId={}, qty={}, user={}", dto.getGroupbuyId(), dto.getJoinQty(), userId);
		groupbuyAdminService.enterGroupbuy(dto.getGroupbuyId(), userId, dto.getJoinQty(), GroupbuyJoinStatus.JOIN);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok("Success"));
	}

	/** 공동구매 예약 취소 */
	@PostMapping("/cancel")
	public ResponseEntity<ApiResponse<Object>> cancel(@RequestBody GroupbuyJoinRequestDto dto, @AuthenticationPrincipal Jwt jwt) {
		String userId = resolveUserId(jwt);
		dto.setUserId(userId);
		log.info("[MOBILE][GROUPBUY][CANCEL] groupbuyId={}, user={}", dto.getGroupbuyId(), userId);
		groupbuyAdminService.enterGroupbuy(dto.getGroupbuyId(), userId, 0, GroupbuyJoinStatus.CANCEL);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok("Success"));
	}

	/** 공유하기 클릭 트래킹 (비로그인 허용, userId 는 JWT 있으면 취득) */
	@PostMapping("/share-log")
	public ResponseEntity<ApiResponse<Object>> shareLog(@RequestBody java.util.Map<String,Object> body, @AuthenticationPrincipal Jwt jwt) {
		String userId = resolveUserId(jwt);
		Long groupbuyId = body.get("groupbuyId") == null ? null : Long.valueOf(String.valueOf(body.get("groupbuyId")));
		Long storeId = body.get("storeId") == null ? null : Long.valueOf(String.valueOf(body.get("storeId")));
		Long locationId = body.get("locationId") == null ? null : Long.valueOf(String.valueOf(body.get("locationId")));
		String shareMethod = body.get("shareMethod") == null ? "OPEN" : String.valueOf(body.get("shareMethod"));
		log.info("[MOBILE][GROUPBUY][SHARE] gid={}, method={}, user={}", groupbuyId, shareMethod, userId);
		groupbuyAdminService.logGroupbuyShare(groupbuyId, storeId, locationId, userId, shareMethod);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok("Success"));
	}

	/**
	 * 공동구매 예약 완료처리 : POS 결제창 연동 (my page 에서 결제창 호출).
	 * ******* 중요 : 결제창 호출 시 POS 에서 수량 및 금액 조정 불가하게 셋팅해야 함.
	 * POS 결제연동 스펙 미확정으로 현재 미구현 — 501 반환.
	 */
	@PostMapping("/complete")
	public ResponseEntity<ApiResponse<Object>> complete(@RequestBody GroupbuyJoinRequestDto dto, @AuthenticationPrincipal Jwt jwt) {
		String userId = resolveUserId(jwt); // 인증 강제
		log.warn("[MOBILE][GROUPBUY][COMPLETE] 미구현 호출 groupbuyId={}, user={}", dto.getGroupbuyId(), userId);
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
				.body(ApiResponse.fail("공동구매 결제완료(POS 결제연동)는 준비 중입니다."));
	}
}
