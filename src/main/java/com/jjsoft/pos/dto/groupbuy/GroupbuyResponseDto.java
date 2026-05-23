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

    private String groupbuyNm;
    private GroupbuyType groupbuyType;

    private Integer targetQty;
    private Integer currentQty;

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
