package com.jjsoft.pos.controller.mobile;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jjsoft.pos.response.ApiResponse;
import com.jjsoft.pos.service.MobileConsentService;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * 마이페이지 선택동의(카카오 선택 프로필) 관리.
 * 모든 엔드포인트는 JWT 인증 사용자 본인 기준으로만 동작한다(SecurityConfig anyRequest().authenticated()).
 */
@RestController
@RequestMapping("/api/mobile/consent")
@RequiredArgsConstructor
@Log4j2
public class MobileConsentController {

    private final MobileConsentService consentService;

    /** 현재 사용자의 선택동의 항목 상태 조회 */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Object>> status(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            return ResponseEntity.ok(ApiResponse.fail("로그인이 필요합니다."));
        }
        String userId = jwt.getClaim("preferred_username");
        return ResponseEntity.ok(ApiResponse.ok(consentService.getStatus(userId)));
    }

    /** 선택동의 철회: 카카오 동의철회 + 해당 데이터 삭제 */
    @PostMapping("/revoke")
    public ResponseEntity<ApiResponse<Object>> revoke(@AuthenticationPrincipal Jwt jwt,
                                                      @RequestBody ConsentScopesRequest body) {
        if (jwt == null) {
            return ResponseEntity.ok(ApiResponse.fail("로그인이 필요합니다."));
        }
        String userId = jwt.getClaim("preferred_username");
        boolean ok = consentService.revoke(userId, body == null ? null : body.getScopes());
        return ok
                ? ResponseEntity.ok(ApiResponse.ok(true))
                : ResponseEntity.ok(ApiResponse.fail("동의 철회 처리에 실패했습니다. 잠시 후 다시 시도해주세요."));
    }

    /** 추가동의용 카카오 authorize URL 발급(프론트가 리다이렉트) */
    @GetMapping("/reconsent-url")
    public ResponseEntity<ApiResponse<Object>> reconsentUrl(@AuthenticationPrincipal Jwt jwt,
                                                            @RequestParam("scope") String scope) {
        if (jwt == null) {
            return ResponseEntity.ok(ApiResponse.fail("로그인이 필요합니다."));
        }
        String url = consentService.buildReconsentUrl(scope);
        return url != null
                ? ResponseEntity.ok(ApiResponse.ok(url))
                : ResponseEntity.ok(ApiResponse.fail("지원하지 않는 동의 항목입니다."));
    }

    /** 추가동의 콜백: code 교환 → 데이터 재수집 → 적재 */
    @PostMapping("/reconsent-callback")
    public ResponseEntity<ApiResponse<Object>> reconsentCallback(@AuthenticationPrincipal Jwt jwt,
                                                                 @RequestBody ReconsentCallbackRequest body) {
        if (jwt == null) {
            return ResponseEntity.ok(ApiResponse.fail("로그인이 필요합니다."));
        }
        String userId = jwt.getClaim("preferred_username");
        boolean ok = consentService.reconsentCallback(userId,
                body == null ? null : body.getCode(),
                body == null ? null : body.getScope());
        return ok
                ? ResponseEntity.ok(ApiResponse.ok(true))
                : ResponseEntity.ok(ApiResponse.fail("추가 동의 처리에 실패했습니다. 잠시 후 다시 시도해주세요."));
    }

    @Data
    public static class ConsentScopesRequest {
        private List<String> scopes;
    }

    @Data
    public static class ReconsentCallbackRequest {
        private String code;
        private String scope;
    }
}
