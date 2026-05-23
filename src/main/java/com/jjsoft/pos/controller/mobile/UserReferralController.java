package com.jjsoft.pos.controller.mobile;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jjsoft.pos.dto.mobile.ReferralResponseDto;
import com.jjsoft.pos.dto.mobile.ReferralSaveRequest;
import com.jjsoft.pos.response.ApiResponse;
import com.jjsoft.pos.service.mobile.UserReferralService;

import lombok.RequiredArgsConstructor;

/**
 * 추천인 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mobile/public")
public class UserReferralController {

	private final UserReferralService userReferralService;

	@GetMapping("/referral/user/today-join")
	public boolean isTodayJoin(@RequestParam("userId") String userId) {

	    return userReferralService.isTodayJoin(userId);
	}
	
	
    /**
     * 추천인 등록
     */
    @PostMapping("/referral/save")
    public boolean saveReferral(@RequestBody ReferralSaveRequest req) {

        return userReferralService.saveReferral(req );
    }
    
    
    @GetMapping("/referral/info")
    public ResponseEntity<ApiResponse<ReferralResponseDto>> getReferralInfo(@AuthenticationPrincipal Jwt jwt) {
    	if (jwt != null) {
	        String userId = jwt.getClaim("email"); // email preferred_username
	        ReferralResponseDto resDto = userReferralService.getReferralInfo(userId );
	        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(resDto));
	    }
    	
    	return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail("인증 실패"));
    }
}
