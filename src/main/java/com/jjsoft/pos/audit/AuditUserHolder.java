package com.jjsoft.pos.audit;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditUserHolder {

    /**
     * 현재 로그인한 사용자 ID(email) 반환
     */
    public static String getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                return authentication.getName(); // Keycloak username(email)
            }
        } catch (Exception e) {
            // 로그인 안 된 경우
        }
        return "ANONYMOUS";
    }
}
