package com.jjsoft.pos.security;

/**
 * 점포 접근 권한 위반(수평 권한 상승 차단). HTTP 403 으로 매핑된다.
 * {@code GlobalExceptionHandler} 에서 처리.
 */
public class StoreAccessDeniedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public StoreAccessDeniedException(String message) {
        super(message);
    }
}
