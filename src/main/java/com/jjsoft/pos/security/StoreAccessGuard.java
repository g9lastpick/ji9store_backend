package com.jjsoft.pos.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 점포 접근 검증/주입 헬퍼. 서비스 계층(특히 @RequestBody 기반 쓰기 API)에서 호출한다.
 *
 * <p>안전한 롤아웃을 위해 {@code multistore.enforce-store-access} 플래그로 강제 여부를 제어한다.
 * <ul>
 *   <li>false(기본, 감사 모드): 위반을 로그로만 남기고 통과 — 기존 관리자 잠금 방지, 매핑 시딩 기간용</li>
 *   <li>true(강제 모드): 위반 시 403({@link StoreAccessDeniedException})</li>
 * </ul>
 * 매핑 시딩 완료 후 운영에서 true 로 전환한다.</p>
 */
@Slf4j
@Component
public class StoreAccessGuard {

    @Value("${multistore.enforce-store-access:false}")
    private boolean enforce;

    public boolean isEnforced() {
        return enforce;
    }

    /**
     * 요청한 점포에 접근 가능한지 검증. 위반 시 강제 모드면 403.
     * 본사(super)는 항상 통과.
     */
    public void assertAccess(Long storeId) {
        if (StoreContext.canAccess(storeId)) {
            return;
        }
        // 컨텍스트 미해석(인증 안 됨 등)도 위반으로 본다.
        String msg = "점포 접근 권한 없음: storeId=" + storeId;
        if (enforce) {
            log.warn("⛔ {} (차단)", msg);
            throw new StoreAccessDeniedException(msg);
        }
        log.warn("⚠️ {} (감사 모드 — 통과)", msg);
    }

    /**
     * 클라이언트가 보낸 storeId 를 검증하고, 누락 시 단일 매핑 점포로 강제한다.
     * @param requested 클라이언트가 보낸 storeId (null 가능)
     * @return 적용할 점포 ID (본사+미지정이면 null = 전체 의미)
     */
    public Long resolveStoreId(Long requested) {
        if (requested != null) {
            assertAccess(requested);
            return requested;
        }
        // 미지정: 본사는 전체(null), 일반 사용자는 단일 매핑 점포로 강제
        Long single = StoreContext.singleAllowedStoreOrNull();
        if (single != null) {
            return single;
        }
        if (!StoreContext.isSuperAdmin() && enforce) {
            throw new StoreAccessDeniedException("점포가 지정되지 않았고 단일 점포로 특정할 수 없습니다.");
        }
        return null;
    }
}
