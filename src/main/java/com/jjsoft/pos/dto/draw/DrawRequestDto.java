package com.jjsoft.pos.dto.draw;

import java.time.LocalDateTime;

import com.jjsoft.pos.dto.groupbuy.GroupbuyResponseDto;
import com.jjsoft.pos.enums.DrawStatus;
import com.jjsoft.pos.enums.GroupbuyStatus;
import com.jjsoft.pos.enums.GroupbuyType;
import com.jjsoft.pos.enums.PayType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 드로우 등록/수정 요청 DTO */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DrawRequestDto {

    private Long storeId;
    private Long locationId;

    private String drawNm;
    private String drawUrl;

    private Long productId;
    
    private LocalDateTime entryStartDate;
    private LocalDateTime entryEndDate;
    
    private LocalDateTime drawDate;
    
    private LocalDateTime pickupStartDate;
    private LocalDateTime pickupEndDate;

    private Integer winnerCnt;

    private Integer totalQty;
    private Integer currentQty;
    private Integer limitQty;
    private Integer salesPrice;
    
    private DrawStatus status;

    private String description;
}
