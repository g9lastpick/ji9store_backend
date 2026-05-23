package com.jjsoft.pos.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.jjsoft.pos.dto.review.ReviewRequestDto;
import com.jjsoft.pos.dto.review.ReviewResponseDto;
import com.jjsoft.pos.dto.review.ReviewSummaryDto;
import com.jjsoft.pos.enums.ResponseCode;
import com.jjsoft.pos.exception.GlobalException;
import com.jjsoft.pos.response.ApiResponse;
import com.jjsoft.pos.service.ReviewService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/** 상품 리뷰 (모바일/공개 + 인증 작성) */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Log4j2
public class ReviewController {

    private final ReviewService reviewService;

    /** 상품별 리뷰 목록 (공개 GET) */
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> list(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @AuthenticationPrincipal Jwt jwt) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), parseSort(sort));
        String callerUserId = jwt == null ? null : jwt.getSubject();
        Page<ReviewResponseDto> result = reviewService.listByProduct(productId, callerUserId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /** 상품 리뷰 요약 (공개 GET) */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Object>> summary(@RequestParam Long productId) {
        ReviewSummaryDto s = reviewService.summary(productId);
        return ResponseEntity.ok(ApiResponse.ok(s));
    }

    /** 여러 상품 요약 batch (모바일 상품 목록용) — 예: /summary/batch?productIds=1,2,3 */
    @GetMapping("/summary/batch")
    public ResponseEntity<ApiResponse<Object>> summaryBatch(@RequestParam List<Long> productIds) {
        Map<Long, ReviewSummaryDto> result = reviewService.summaries(productIds);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /** 작성 (인증 필수) */
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> create(
            @RequestBody ReviewRequestDto req,
            @AuthenticationPrincipal Jwt jwt) {
        requireAuth(jwt);
        ReviewResponseDto result = reviewService.create(req, jwt.getSubject(), nickname(jwt));
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /** 수정 (인증 + 본인) */
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Object>> update(
            @PathVariable Long reviewId,
            @RequestBody ReviewRequestDto req,
            @AuthenticationPrincipal Jwt jwt) {
        requireAuth(jwt);
        ReviewResponseDto result = reviewService.update(reviewId, req, jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /** 삭제 (인증 + 본인, soft delete) */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Object>> delete(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal Jwt jwt) {
        requireAuth(jwt);
        reviewService.delete(reviewId, jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    /** 도움됐어요 토글 (인증) */
    @PostMapping("/{reviewId}/helpful")
    public ResponseEntity<ApiResponse<Object>> toggleHelpful(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal Jwt jwt) {
        requireAuth(jwt);
        ReviewResponseDto result = reviewService.toggleHelpful(reviewId, jwt.getSubject());
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // helpers
    private void requireAuth(Jwt jwt) {
        if (jwt == null) throw new GlobalException(ResponseCode.UNAUTHORIZED, "로그인이 필요합니다");
    }

    /**
     * Keycloak 토큰의 표시명 우선순위:
     * name > nickname > given_name > preferred_username
     * (이메일이 저장되지 않도록 — preferred_username은 보통 이메일이므로 마지막 fallback)
     */
    private String nickname(Jwt jwt) {
        String name = jwt.getClaimAsString("name");
        if (isBlank(name)) name = jwt.getClaimAsString("nickname");
        if (isBlank(name)) name = jwt.getClaimAsString("given_name");
        if (isBlank(name)) name = jwt.getClaimAsString("preferred_username");
        return name;
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private Sort parseSort(String sortParam) {
        String[] parts = sortParam.split(",");
        String field = parts[0];
        Sort.Direction dir = (parts.length > 1 && "asc".equalsIgnoreCase(parts[1])) ? Sort.Direction.ASC : Sort.Direction.DESC;
        switch (field) {
            case "rating":         return Sort.by(dir, "rating");
            case "helpful":
            case "helpfulCount":   return Sort.by(dir, "helpfulCount");
            case "createdAt":
            default:               return Sort.by(dir, "createdAt");
        }
    }
}
