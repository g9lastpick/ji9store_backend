package com.jjsoft.pos.security;

import java.io.IOException;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 요청마다 StoreContext 를 채우고(인증 이후) 종료 시 비운다.
 * Spring Security 필터 체인 이후 실행되도록 보장하기 위해 가장 마지막 순서로 둔다.
 */
@Component
@Order(Integer.MAX_VALUE)
@RequiredArgsConstructor
public class StoreAccessResolverFilter extends OncePerRequestFilter {

    private final StoreAccessResolver resolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            resolver.populate();
            chain.doFilter(request, response);
        } finally {
            StoreContext.clear();
        }
    }

    /** 점포 컨텍스트가 필요 없는 공개/정적 경로는 스킵 */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/health")
                || uri.startsWith("/upload/")
                || uri.startsWith("/api/image/")
                || uri.startsWith("/api/public/")
                || uri.startsWith("/api/mobile/public/")
                || uri.startsWith("/docs/")
                || uri.startsWith("/swagger-ui/");
    }
}
