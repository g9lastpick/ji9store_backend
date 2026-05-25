package com.jjsoft.pos.keycloak;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.jjsoft.pos.enums.ResponseCode;
import com.jjsoft.pos.exception.GlobalException;

public final class JwtPrincipalUtils {

    private JwtPrincipalUtils() {}

    public static Jwt currentJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken token) {
            return token.getToken();
        }
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        }
        throw new GlobalException(ResponseCode.UNAUTHORIZED, "JWT 인증 정보가 없습니다.");
    }

    public static Long requireStoreId() {
        Jwt jwt = currentJwt();
        Object claim = jwt.getClaim("store_id");
        if (claim == null) {
            throw new GlobalException(ResponseCode.UNAUTHORIZED, "JWT에 store_id claim이 없습니다.");
        }
        try {
            if (claim instanceof Number n) return n.longValue();
            return Long.parseLong(claim.toString().trim());
        } catch (NumberFormatException e) {
            throw new GlobalException(ResponseCode.UNAUTHORIZED, "JWT store_id claim이 유효하지 않습니다.");
        }
    }
}
