package com.jjsoft.pos.audit;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.jjsoft.pos.entity.AuditLogEntity;
import com.jjsoft.pos.repository.AuditLogRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final HttpServletRequest request;
    private final AuditLogRepository auditLogRepository;

    /**
     * 모든 Controller 메서드 호출 후 실행
     * (성공 응답 이후 로그를 남김) get은 너무 많이 쌓여서 생략함... 추후 협의 예정
     */
    @AfterReturning("execution(* com.jjsoft.pos.controller..*(..))")
    public void logUserAction(JoinPoint joinPoint) {
        try {
        	
        	String method = request.getMethod();
        	Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        	String userId   = jwt.getClaimAsString("email"); // 또는 sub, email
        	String name     = jwt.getClaimAsString("name");
        	
            String url = request.getRequestURI();
            // get요청시 메인화면 유저별 하루 한번만 저장 
            // ✅ GET 요청은 Audit 로그 제외
            if ("GET".equalsIgnoreCase(method)) {
                return;
            }
        	
            String params = Arrays.toString(joinPoint.getArgs());
            AuditLogEntity logEntity = AuditLogEntity.builder()
                    .userId(userId)
                    .userName(name)
                    .actionType(method)
                    .url(url)
                    .params(params)
                    .build();

            auditLogRepository.save(logEntity);

            log.debug("✅ Audit Log saved: {} {} {}", userId, method, url);

        } catch (Exception e) {
            log.error("⚠️ Audit 로그 저장 실패", e);
        }
    }
}
