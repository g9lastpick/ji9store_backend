package com.jjsoft.pos.controller.mobile;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jjsoft.pos.response.ApiResponse;
import com.jjsoft.pos.service.mobile.MobileMyPageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * 모바일 My Page
 */
@RestController
@RequestMapping("/api/mobile/mypage")
@RequiredArgsConstructor
@Log4j2
public class MobileMyPageController {

	private final MobileMyPageService mobileMyPageService;

	/** 기간별 결재내역 및 결재 취소내역 , 예약 취소내역 조회 */

	/** 예약상품(공동구매 / 특가상품 등...) 조회 조회 */

	/** QR 코드 발급 */

	/**
	 * 「지구 한바퀴」 지역 스탬프 조회.
	 * 로그인 사용자가 구매한 상품의 계열사 소재지를 7권역으로 환산해 스탬프 상태를 반환한다.
	 */
	@GetMapping("/regionStamps")
	public ResponseEntity<ApiResponse<Object>> regionStamps(@RequestParam("storeId") Long storeId,
			@AuthenticationPrincipal Jwt jwt) {
		String userId = (jwt != null) ? jwt.getClaim("email") : null; // sales_mst.USER_ID = email
		return ResponseEntity.status(HttpStatus.OK)
				.body(ApiResponse.ok(mobileMyPageService.getRegionStamps(userId, storeId)));
	}

}
