package com.jjsoft.pos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jjsoft.pos.response.ApiResponse;
import com.jjsoft.pos.security.TokenDenylistService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * 로그아웃 API. 현재 access token(jti)을 만료시각까지 거부목록에 등록 →
 * 동일 토큰의 이후 API 접근을 즉시 차단(401). 클라이언트는 이 호출 후 Keycloak end-session 으로 이동한다.
 * 인증 필요 라우트(anyRequest authenticated)이므로 토큰 없으면 호출 자체가 401.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class AuthController {

    private final TokenDenylistService denylistService;

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@AuthenticationPrincipal Jwt jwt) {
        if (jwt != null) {
            String jti = jwt.getId();
            long exp = (jwt.getExpiresAt() != null)
                    ? jwt.getExpiresAt().toEpochMilli()
                    : System.currentTimeMillis();
            denylistService.revoke(jti, exp);
            log.info("logout: access token revoked (jti={})", jti);
        }
        return ResponseEntity.ok(ApiResponse.ok("logged out"));
    }
}
