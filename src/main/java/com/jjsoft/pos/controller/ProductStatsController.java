package com.jjsoft.pos.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jjsoft.pos.repository.SalesMstRepository;
import com.jjsoft.pos.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/** 모바일 상품 카드용 공개 통계 (누적 판매 건수 batch). */
@RestController
@RequestMapping("/api/products/stats")
@RequiredArgsConstructor
@Log4j2
public class ProductStatsController {

    private final SalesMstRepository salesMstRepository;

    /**
     * 상품 ID 여러 개의 누적 판매 건수를 한 번에 반환.
     * 예: GET /api/products/stats/sales-count/batch?productIds=1,2,3
     * 응답: { "1": 134, "2": 21, "3": 0 }
     */
    @GetMapping("/sales-count/batch")
    public ResponseEntity<ApiResponse<Object>> salesCountBatch(@RequestParam List<Long> productIds) {
        Map<Long, Long> result = new LinkedHashMap<>();
        if (productIds != null && !productIds.isEmpty()) {
            for (Long pid : productIds) if (pid != null) result.put(pid, 0L);
            for (Object[] row : salesMstRepository.countSalesByProductIds(productIds)) {
                Long productId = ((Number) row[0]).longValue();
                Long cnt = ((Number) row[1]).longValue();
                result.put(productId, cnt);
            }
        }
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
