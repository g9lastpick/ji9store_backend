package com.jjsoft.pos.security;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import com.jjsoft.pos.entity.UserMstEntity;
import com.jjsoft.pos.entity.UserStoreMapEntity;
import com.jjsoft.pos.repository.UserMstRepository;
import com.jjsoft.pos.repository.UserStoreMapRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 인증 주체(JWT)를 점포 접근 권한으로 해석한다.
 *
 * <p>등급(전역 Keycloak role): {@code SUPER_ADMIN} 또는 기존 {@code ADMIN} = 본사(전 점포).
 * 그 외(MANAGER/STORE_ADMIN/STAFF)는 {@code user_store_map} 에 매핑된 점포로 한정.</p>
 *
 * <p>연결 키: JWT {@code email} → {@code user_mst.email} → {@code user_mst.id} → {@code user_store_map}.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StoreAccessResolver {

    /** 본사(전 점포) 등급. 기존 ADMIN 은 하위 호환을 위해 본사로 간주(잠금 방지). */
    private static final Set<String> SUPER_ROLES = Set.of("ROLE_SUPER_ADMIN", "ROLE_ADMIN");

    private final UserMstRepository userMstRepository;
    private final UserStoreMapRepository userStoreMapRepository;

    /** SecurityContext 의 인증 주체로부터 StoreContext 를 채운다. 인증/JWT 가 없으면 채우지 않음. */
    public void populate() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof JwtAuthenticationToken token)) {
            return;
        }

        boolean superAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(SUPER_ROLES::contains);

        Jwt jwt = token.getToken();
        String email = jwt.getClaimAsString("email");

        Long userId = null;
        Set<Long> allowed = new HashSet<>();

        if (email != null) {
            Optional<UserMstEntity> userOpt = userMstRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                userId = userOpt.get().getId();
                List<UserStoreMapEntity> maps = userStoreMapRepository.findByUserIdAndUseYn(userId, "Y");
                allowed = maps.stream()
                        .map(UserStoreMapEntity::getStoreId)
                        .filter(java.util.Objects::nonNull)
                        .collect(Collectors.toSet());
            }
        }

        if (!superAdmin && allowed.isEmpty()) {
            log.debug("점포 매핑 없음 — email={}, 비-본사 사용자. 점포 스코프 요청은 차단 대상.", email);
        }

        StoreContext.set(userId, superAdmin, allowed);
    }
}
