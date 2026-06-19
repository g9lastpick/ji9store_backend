package com.jjsoft.pos.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

/**
 * 로그아웃된 access token(jti) 거부 목록 (in-memory).
 *
 * stateless JWT(서명+exp만 검증) 환경에서는 이미 발급된 access token이 로그아웃 후에도
 * 만료(exp)까지 유효하다. 본 서비스는 로그아웃 시 해당 토큰의 jti를 만료시각까지 등록해두고,
 * 이후 동일 토큰의 모든 API 접근을 차단(401)하여 "로그아웃 즉시 Authorization 헤더 무효화"를 구현한다.
 *
 * 항목은 토큰 exp 시점에 자동 정리되므로 메모리는 발급 토큰 수명(최대 30분) 분량으로 제한된다.
 */
@Service
public class TokenDenylistService {

    // jti -> 만료시각(epoch millis)
    private final Map<String, Long> denylist = new ConcurrentHashMap<>();

    /** 로그아웃: jti를 토큰 만료시각까지 거부목록에 등록 */
    public void revoke(String jti, long expEpochMillis) {
        if (jti == null || jti.isBlank()) {
            return;
        }
        sweep();
        denylist.put(jti, expEpochMillis);
    }

    /** 거부된(로그아웃된) 토큰인지 확인. 이미 만료된 항목은 자동 정리. */
    public boolean isRevoked(String jti) {
        if (jti == null) {
            return false;
        }
        Long exp = denylist.get(jti);
        if (exp == null) {
            return false;
        }
        if (exp <= System.currentTimeMillis()) {
            denylist.remove(jti);
            return false;
        }
        return true;
    }

    /** 만료된 jti 정리 */
    private void sweep() {
        long now = System.currentTimeMillis();
        denylist.entrySet().removeIf(e -> e.getValue() <= now);
    }
}
