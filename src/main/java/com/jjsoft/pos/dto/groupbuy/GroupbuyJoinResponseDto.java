package com.jjsoft.pos.dto.groupbuy;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 공동구매 예약(참여) 조회 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupbuyJoinResponseDto {

	 private Long   groupbuyJoinMstId;

    private String userId;
    private String userNm;

    private String tmpUserId;
    private String tmpPhoneNo;

    private String joinStatus;

    private Integer totalQty;
    private Integer totalPrice;
    private Integer unitPrice;

    private Long   salesId;

    private LocalDateTime createDate;

    /* === 모바일 마이페이지 확장 필드 === */
    private Long groupbuyId;
    private String groupbuyNm;
    private String groupbuyStatus;
    private Long productId;
    private String productNm;
    private Integer orgSalesPrice;
    private String imageUrl;
    private Integer curUnitPrice;
    private Integer targetQty;
    private Integer currentQty;
    private LocalDateTime pickupStartDate;
    private LocalDateTime pickupEndDate;
}
