package com.jjsoft.pos.dto.groupbuy;

import java.time.LocalDateTime;
import java.util.List;

import com.jjsoft.pos.enums.GroupbuyStatus;
import com.jjsoft.pos.enums.GroupbuyType;
import com.jjsoft.pos.enums.PayType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 공동구매 조회 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupbuyResponseDto {

    private Long groupbuyId;

    private Long storeId;
    private String storeNm;

    private Long locationId;
    private String locationNm;
    
    private Long productId;
    private String productNm;
    private Integer orgSalesPrice;

    /** 모바일 노출용: 최저 단계가 / 최대 할인율 */
    private Integer lowestSalesPrice;
    private java.math.BigDecimal maxDiscountRate;

    /** 대표 이미지(공동구매 이미지 없으면 상품 썸네일) */
    private String imageUrl;
    /** 가격 구간(모바일 목록에서 함께 내려줌) */
    private java.util.List<GroupbuyPriceStepDto> priceSteps;

    private String groupbuyNm;
    private GroupbuyType groupbuyType;

    private Integer targetQty;
    private Integer currentQty;

    /** 1인당 구매 제한 수량 (0/null = 무제한) */
    private Integer limitQty;

    private Integer targetAmount;
    private Integer currentAmount;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime pickupStartDate;
    private LocalDateTime pickupEndDate;

    private GroupbuyStatus status;
    private PayType payType;

    private String description;
    
}
