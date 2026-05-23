package com.jjsoft.pos.dto.review;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewSummaryDto {
    private Long productId;
    private long totalCount;
    private double averageRating;     // 소수점 한 자리 권장
    private Map<Integer, Long> ratingDistribution;  // {5: 12, 4: 3, ...}
}
