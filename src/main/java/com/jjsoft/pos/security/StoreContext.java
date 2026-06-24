package com.jjsoft.pos.security;

import java.util.Collections;
import java.util.Set;

/**
 * 요청 스코프 점포 컨텍스트.
 *
 * <p>인증 주체(JWT)로부터 해석된 "접근 가능한 점포 집합"과 등급(SUPER_ADMIN 여부)을 담는다.
 * 컨트롤러/서비스/감사로그가 클라이언트가 보낸 storeId 를 신뢰하는 대신 이 컨텍스트로 검증·주입한다.</p>
 *
 * <p>{@link StoreAccessResolverFilter} 가 요청 시작 시 채우고 종료 시 비운다(ThreadLocal 누수 방지).</p>
 */
public final class StoreContext {

    private StoreContext() {}

    public static final class Ctx {
        /** 내부 유저 ID (user_mst.id). 미해석 시 null */
        public final Long userId;
        /** 전역 본사 등급 — 모든 점포 접근 허용 */
        public final boolean superAdmin;
        /** 접근 가능한 점포 ID 집합 (superAdmin 이면 의미 없음) */
        public final Set<Long> allowedStoreIds;

        Ctx(Long userId, boolean superAdmin, Set<Long> allowedStoreIds) {
            this.userId = userId;
            this.superAdmin = superAdmin;
            this.allowedStoreIds = allowedStoreIds == null
                    ? Collections.emptySet()
                    : Collections.unmodifiableSet(allowedStoreIds);
        }
    }

    private static final ThreadLocal<Ctx> HOLDER = new ThreadLocal<>();

    public static void set(Long userId, boolean superAdmin, Set<Long> allowedStoreIds) {
        HOLDER.set(new Ctx(userId, superAdmin, allowedStoreIds));
    }

    public static Ctx get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }

    public static boolean isSuperAdmin() {
        Ctx c = HOLDER.get();
        return c != null && c.superAdmin;
    }

    /** 컨텍스트가 해석되어 있는지 (인증된 관리자/공통 요청) */
    public static boolean isResolved() {
        return HOLDER.get() != null;
    }

    /** 해당 점포에 접근 가능한지. 컨텍스트 미해석이면 false (호출 측에서 판단). */
    public static boolean canAccess(Long storeId) {
        Ctx c = HOLDER.get();
        if (c == null) {
            return false;
        }
        if (c.superAdmin) {
            return true;
        }
        return storeId != null && c.allowedStoreIds.contains(storeId);
    }

    /** 단일 점포로 강제할 수 있으면 그 점포 ID, 아니면 null (super 또는 다중/없음). */
    public static Long singleAllowedStoreOrNull() {
        Ctx c = HOLDER.get();
        if (c == null || c.superAdmin) {
            return null;
        }
        return c.allowedStoreIds.size() == 1 ? c.allowedStoreIds.iterator().next() : null;
    }
}
