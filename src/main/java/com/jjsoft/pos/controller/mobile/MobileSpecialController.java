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
	 * */
	@GetMapping("/memberOut")
	public ResponseEntity<ApiResponse<Object>> memberOut( @AuthenticationPrincipal Jwt jwt) {
		
		try {
			if (jwt != null) {
				String userId = jwt.getClaim("preferred_username"); // email preferred_username
				
				UserMstEntity userEntity = userMstRepository.getUserByUserId(userId).orElse(null);
		        if(userEntity != null) {
		        	userEntity.setUseYn("N");
		        }
				
				// 카카오 api 동의 철회
				keycloakService.kakaoMemberOut(userId);
				
				keycloakService.deleteUser(userId);
		    }
			return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(true));
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(false));
		}
		
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
