package com.jjsoft.pos.dto.groupbuy;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 공동구매 상세 조회 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupbuyDetailResponseDto {

	private Long groupbuyId;

	private Long storeId;
	private String storeNm;

	private Long locationId;
	private String locationNm;

	private Long productId;
	private String productNm;
	private Integer orgSalesPrice;

	private String groupbuyNm;
	private String groupbuyType;

	private Integer targetQty;
	private Integer currentQty;

	private Integer targetAmount;
	private Integer currentAmount;

	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private LocalDateTime pickupStartDate;
	private LocalDateTime pickupEndDate;

	private String pickupMode;

	private String status;
	private String payType;

	private Integer limitQty;
	private String imageUrl;

	private String description;

	private List<GroupbuyPriceStepDto> priceSteps;
	
	
	private Integer curStockQty;//현재 남은 재고 수량
}
