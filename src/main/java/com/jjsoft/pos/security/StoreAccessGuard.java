package com.jjsoft.pos.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jjsoft.pos.keycloak.JwtPrincipalUtils;

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
     * 읽기 조회의 storeId 를 인증 주체 기준으로 확정한다.
     * 클라이언트가 보낸 storeId 는 신뢰하지 않고, 누락(전 점포 노출) 취약점을 닫는다.
     *
     * <ul>
     *   <li>본사(SUPER_ADMIN/ADMIN): 요청값 그대로(특정 점포 보기) 또는 null(전체 조회) — 전 점포 권한</li>
     *   <li>그 외: 본인 점포로 강제. 우선순위 = user_store_map 단일 점포 → JWT {@code store_id} claim.
     *       요청값이 본인 점포와 다르면 위반 처리(강제 모드 403). 본인 점포 특정 불가 시 강제 모드 403.</li>
     * </ul>
     *
     * @param requested 클라이언트가 보낸 storeId (null 가능)
     * @return 쿼리에 적용할 점포 ID (본사 전체 조회면 null)
     */
    public Long resolveStoreId(Long requested) {
        // 본사: 전 점포. 선택한 점포가 있으면 그 점포, 없으면 전체(null)
        if (StoreContext.isSuperAdmin()) {
            return requested;
        }

        // 비-본사: 본인 점포 확정 (user_store_map 단일 → JWT store_id claim 폴백)
        Long own = StoreContext.singleAllowedStoreOrNull();
        if (own == null) {
            own = jwtStoreIdOrNull();
        }

        if (own != null) {
            // 요청값이 본인 점포(또는 허용 점포)와 어긋나면 위반
            if (requested != null && !requested.equals(own) && !StoreContext.canAccess(requested)) {
                deny("요청 storeId=" + requested + " 가 본인 점포(" + own + ")와 다름");
            }
            return own; // 누락이든 일치든 본인 점포로 강제 → 전 점포 노출 차단
        }

        // 본인 점포를 특정할 수 없음 (매핑 없음 + claim 없음, 또는 다중 점포 미지정)
        if (StoreContext.canAccess(requested)) {
            return requested; // 다중 점포 사용자가 허용 점포를 명시한 경우
        }
        deny("본인 점포를 특정할 수 없고 요청 storeId=" + requested + " 도 허용되지 않음");
        return requested; // 감사 모드에서만 도달 (deny 가 통과시킴)
    }

    private void deny(String reason) {
        if (enforce) {
            log.warn("⛔ 점포 접근 위반: {} (차단)", reason);
            throw new StoreAccessDeniedException(reason);
        }
        log.warn("⚠️ 점포 접근 위반: {} (감사 모드 — 통과)", reason);
    }

    /** JWT 의 store_id claim (단일 점포 관리자용 폴백). 없으면 null. */
    private Long jwtStoreIdOrNull() {
        try {
            return JwtPrincipalUtils.requireStoreId();
        } catch (RuntimeException e) {
            return null;
        }
    }
}
