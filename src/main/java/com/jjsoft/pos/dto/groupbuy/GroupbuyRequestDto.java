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
 * 공동구매 등록/수정 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupbuyRequestDto {

    private Long storeId;
    private Long locationId;
    private Long productId;
    private String groupbuyNm;

    private GroupbuyType groupbuyType;
    private GroupbuyStatus status;
    

    private Integer targetQty;
    private Integer targetAmount;
    private Integer limitQty;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime pickupStartDate;
    private LocalDateTime pickupEndDate;

    private PayType payType;

    private com.jjsoft.pos.enums.GroupbuyPickupMode pickupMode;

    private String description;

    private String createUser;
    private String updateUser;
    
    /** 가격 정책 */
    private List<GroupbuyPriceStepDto> priceList;
}
