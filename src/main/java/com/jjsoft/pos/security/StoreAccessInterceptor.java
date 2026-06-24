package com.jjsoft.pos.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 요청 파라미터 {@code storeId} 를 StoreContext 의 허용 점포와 대조한다.
 * 다른 점포의 storeId 를 보낸 경우(수평 권한 상승) 차단.
 *
 * <p>요청 바디(@RequestBody)에 들어오는 storeId 는 본 인터셉터로 검사할 수 없으므로,
 * 그런 쓰기 API 는 서비스 계층에서 {@link StoreAccessGuard#assertAccess(Long)} 를 호출해 검증한다.</p>
 */
@Slf4j
@Component
public class StoreAccessInterceptor implements HandlerInterceptor {

    @Value("${multistore.enforce-store-access:false}")
    private boolean enforce;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String raw = request.getParameter("storeId");
        if (raw == null || raw.isBlank()) {
            return true; // 파라미터 없음 — 바디 기반이거나 비스코프 요청. 서비스 계층/가드에서 처리.
        }

        Long storeId;
        try {
            storeId = Long.valueOf(raw.trim());
        } catch (NumberFormatException e) {
            return true; // 숫자 아님 — 컨트롤러 바인딩 단계에 위임
        }

        if (StoreContext.canAccess(storeId)) {
            return true;
        }

        String msg = "점포 접근 권한 없음: storeId=" + storeId + ", uri=" + request.getRequestURI();
        if (!enforce) {
            log.warn("⚠️ {} (감사 모드 — 통과)", msg);
            return true;
        }

        log.warn("⛔ {} (차단)", msg);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"success\":false,\"message\":\"해당 점포에 대한 접근 권한이 없습니다.\",\"data\":null}");
        return false;
    }
}
