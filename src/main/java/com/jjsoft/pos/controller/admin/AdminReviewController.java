package com.jjsoft.pos.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jjsoft.pos.dto.review.ReviewResponseDto;
import com.jjsoft.pos.response.ApiResponse;
import com.jjsoft.pos.service.ReviewService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/** 어드민 리뷰 관리 — 모니터링/숨김처리 */
@RestController
@RequestMapping("/api/admin/reviews")
@RequiredArgsConstructor
@Log4j2
public class AdminReviewController {

    private final ReviewService reviewService;

    /** 전체/필터 목록 */
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String phoneLast4,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 200), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReviewResponseDto> result = reviewService.adminList(status, productId, phoneLast4, pageable);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /** 상태 변경 (ACTIVE / HIDDEN / DELETED) */
    @PatchMapping("/{reviewId}/status")
    public ResponseEntity<ApiResponse<Object>> changeStatus(
            @PathVariable Long reviewId,
            @RequestParam String status) {
        ReviewResponseDto result = reviewService.adminChangeStatus(reviewId, status);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    /** 유저(sub)별 이벤트 참여 체크 토글 — 동일 유저의 모든 리뷰 행에 반영, 영구 저장 */
    @PostMapping("/event-check")
    public ResponseEntity<ApiResponse<Object>> eventCheck(
            @RequestParam String userId,
            @RequestParam boolean participated) {
        reviewService.setEventParticipation(userId, participated, "admin");
        return ResponseEntity.ok(ApiResponse.ok(true));
    }
}
