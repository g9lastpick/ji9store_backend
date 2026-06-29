package com.jjsoft.pos.controller.mobile;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jjsoft.pos.dto.mobile.ReservationApplyRequestDto;
import com.jjsoft.pos.dto.special.condition.SpecialSearchCondition;
import com.jjsoft.pos.entity.UserMstEntity;
import com.jjsoft.pos.keycloak.KakaoUserInfoDto;
import com.jjsoft.pos.keycloak.KeycloakService;
import com.jjsoft.pos.repository.UserMstRepository;
import com.jjsoft.pos.response.ApiResponse;
import com.jjsoft.pos.service.admin.special.SpecialService;
import com.jjsoft.pos.service.mobile.MobileSpecialService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * 모바일 관련 상품 조회
 */
@RestController
@RequestMapping("/api/mobile/special")
@RequiredArgsConstructor
@Log4j2
public class MobileSpecialController {

	private final MobileSpecialService mobileSpecialService;
	private final SpecialService specialService;
	private final KeycloakService keycloakService;
	private final UserMstRepository userMstRepository;
	private final com.jjsoft.pos.service.UserWithdrawalService userWithdrawalService;
	private final com.jjsoft.pos.service.banner.StripBannerService stripBannerService;

	/** 특가 탭 띠배너 조회 - 기간 내 활성배너(여러 개면 캐러셀), 없으면 기본배너 */
	@GetMapping("/banner")
	public ResponseEntity<ApiResponse<Object>> banner(
			@RequestParam("storeId") Long storeId,
			@RequestParam(value = "date", required = false) String date) {
		java.time.LocalDate day = (date != null && !date.isBlank())
				? java.time.LocalDate.parse(date.trim()) : null;
		return ResponseEntity.status(HttpStatus.OK)
				.body(ApiResponse.ok(stripBannerService.getDisplayBanners(storeId, day)));
	}

	/** 특가 리스트 조회 */
	@GetMapping("/specialList")
	public ResponseEntity<ApiResponse<Object>> specialList(@ModelAttribute SpecialSearchCondition condition) {
//		log.error("특가 리스트 조회  sample error log ###############################################################");
		
		boolean isFirstJoin = mobileSpecialService.isFirstJoin();
		if(isFirstJoin) {
			
			long cnt = userMstRepository.count();
			if(cnt <= 205) {
				String msg = (cnt-4)+"번째 가입을 환영합니다.";
				log.info("최초 메시지 나감. msg = {} " , msg);
				return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(mobileSpecialService.specialList(condition) , true , msg));
			}else {
				String msg = (cnt-4)+"번째 가입을 환영합니다. \n "+ (cnt-4) + "회원기입이라 사은품은 없습니다. ";
				log.info("최초 메시지 나감. msg = {} " , msg);
				return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(mobileSpecialService.specialList(condition) , true , msg));
			}
		}else {
			return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(mobileSpecialService.specialList(condition)));
		}
	}
	/** 현재 로그인 사용자의 가입 점포 조회 (신규/미지정이면 null).
	 *  모바일 멀티점포 진입(/store/:storeId) 시 본인 점포로 강제할지 판단하는 데 사용. */
	@GetMapping("/myStore")
	public ResponseEntity<ApiResponse<Object>> myStore() {
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(mobileSpecialService.getSignupStoreId()));
	}

	/** 사용중인 점포 목록 (최초 가입 점포 선택 화면용). [{storeId, storeNm}] */
	@GetMapping("/storeList")
	public ResponseEntity<ApiResponse<Object>> storeList() {
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(mobileSpecialService.getStoreList()));
	}

	/** 특가 상품 목록 조회 */
	@GetMapping("/specialDetailList")
	public ResponseEntity<ApiResponse<Object>> specialDetailList(@ModelAttribute SpecialSearchCondition condition , @AuthenticationPrincipal Jwt jwt ) {
		if (jwt != null) {
	        String userId = jwt.getClaim("email"); // email preferred_username
	        condition.setUserId(userId);
	    }
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(mobileSpecialService.specialDetailList(condition)));
	}
	/** 특가 예약전 특가수량 조회 */
	@GetMapping("/remainStockList")
	public ResponseEntity<ApiResponse<Object>> specialRemainQtyList(@ModelAttribute SpecialSearchCondition condition ) {
		
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(mobileSpecialService.specialRemainQtyList(condition)));
	}
	
	
	/** 특가 상품 상세 보기 */
	@GetMapping("/productDetailImageList")
	public ResponseEntity<ApiResponse<Object>> productDetailImageList(@ModelAttribute SpecialSearchCondition condition ) {
		
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(mobileSpecialService.productDetailImageList(condition)));
	}
	
//	/** 특가 유저 예약 저장  */
//	@PostMapping("/reservationListSave")
//	public ResponseEntity<ApiResponse<Object>> reservationListSave(@RequestBody List<ReservationItemDto> list) {
//	    boolean flag = specialService.reservationListSave(list);
//	    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(flag));
//	}
	/** 특가 유저 예약 저장  
	 * @throws Exception */
	@PostMapping("/reservationListSave")
	public ResponseEntity<ApiResponse<Object>> reservationListSave(@RequestBody ReservationApplyRequestDto dto , @AuthenticationPrincipal Jwt jwt) throws Exception {
		if (jwt != null) {
			String userId = jwt.getClaim("email"); // email preferred_username
	        dto.setUserId(userId);
	    }
		boolean flag = mobileSpecialService.reservationListSave(dto);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(flag));
	}
	
	/** 상품별 예약 취소 */
	@PostMapping("/cancelReservationItems")
	public ResponseEntity<ApiResponse<Object>> cancelReservationItems(@RequestBody ReservationApplyRequestDto dto , @AuthenticationPrincipal Jwt jwt) {
		if (jwt != null) {
			String userId = jwt.getClaim("email"); // email preferred_username
	        dto.setUserId(userId);
	    }
		boolean flag = mobileSpecialService.cancelReservationItems(dto);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(flag));
	}
	
	
	/** 모바일 상품 목록 조회 - 라스트픽 , 신규상품 , 매장 제고*/
	@GetMapping("/selectMobileProductList")
	public ResponseEntity<ApiResponse<Object>> selectMobileProductList(@ModelAttribute SpecialSearchCondition condition) {
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(mobileSpecialService.selectMobileProductList(condition)));
	}

	
	
	
	/** 회원 탈퇴
	 * 탈퇴시 모든 예약정보 취소 또는 삭제 로직 들어가야함.
	 * 본인 재확인: 탈퇴 직전 재로그인(prompt=login)으로 갱신된 auth_time 의 신선도를 검증한다.
	 * */
	@GetMapping("/memberOut")
	public ResponseEntity<ApiResponse<Object>> memberOut( @AuthenticationPrincipal Jwt jwt) {

		try {
			if (jwt != null) {
				// 본인 재확인 검증: 최근(5분 이내) 재인증한 토큰만 탈퇴 허용
				if (!isReauthenticatedRecently(jwt, 300)) {
					log.warn("memberOut 거부: 재인증 신선도 미충족(auth_time stale)");
					return ResponseEntity.status(HttpStatus.OK)
							.body(ApiResponse.fail("본인 재확인(재로그인) 후 다시 시도해 주세요."));
				}

				String userId = jwt.getClaim("preferred_username"); // email preferred_username
				String keycloakSub = jwt.getClaim("sub");

				// 개인정보 제거 + USER_ID 가명화(WITHDRAWN_). 활동·구매 데이터 행은 보존.
				userWithdrawalService.withdrawMember(userId, keycloakSub);

				// 카카오 api 동의 철회
				keycloakService.kakaoMemberOut(userId);

				keycloakService.deleteUser(userId);
		    }
			return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(true));
		}catch(Exception e) {
			log.error("memberOut 처리 실패", e);
			return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(false));
		}

	}

	/**
	 * 본인 재확인 검증: 토큰의 auth_time(최종 인증 시각)이 maxAgeSeconds 이내인지 확인한다.
	 * 탈퇴 직전 prompt=login 으로 재로그인하면 auth_time 이 갱신되므로,
	 * 이미 로그인된 기기에서 타인이 곧바로 탈퇴하는 것을 차단한다.
	 */
	private boolean isReauthenticatedRecently(Jwt jwt, long maxAgeSeconds) {
		Object authTimeClaim = jwt.getClaim("auth_time");
		if (authTimeClaim == null) {
			return false; // auth_time 이 없으면 재인증 미확인으로 간주
		}

		long authTimeEpoch;
		if (authTimeClaim instanceof java.time.Instant) {
			authTimeEpoch = ((java.time.Instant) authTimeClaim).getEpochSecond();
		} else if (authTimeClaim instanceof java.util.Date) {
			authTimeEpoch = ((java.util.Date) authTimeClaim).toInstant().getEpochSecond();
		} else if (authTimeClaim instanceof Number) {
			authTimeEpoch = ((Number) authTimeClaim).longValue();
		} else {
			try {
				authTimeEpoch = Long.parseLong(authTimeClaim.toString());
			} catch (NumberFormatException e) {
				return false;
			}
		}

		long nowEpoch = java.time.Instant.now().getEpochSecond();
		return (nowEpoch - authTimeEpoch) <= maxAgeSeconds;
	}

	
	
	
	
	/** 모바일 주문내역 조회 2025 10 29 추가내역 */
	@GetMapping("/mobileOrderList")
	public ResponseEntity<ApiResponse<Object>> mobileOrderList(@ModelAttribute SpecialSearchCondition condition , @AuthenticationPrincipal Jwt jwt ) {
		if (jwt != null) {
	        String userId = jwt.getClaim("email"); // email preferred_username
	        condition.setUserId(userId);
	    }
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(mobileSpecialService.mobileOrderList(condition)));
	}
}
