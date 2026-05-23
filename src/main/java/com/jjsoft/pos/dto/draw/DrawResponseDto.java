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

/** 드로우 목록 응답 DTO */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DrawResponseDto {

	/* =========================
     * 기본 키
     * ========================= */

    private Long drawId;

    /* =========================
     * 매장 / 지점 정보
     * ========================= */

    private Long   storeId;
    private String storeNm;

    private Long   locationId;
    private String locationNm;

    /* =========================
     * 상품 정보
     * ========================= */

    private Long   productId;
    private String productNm;

    /* =========================
     * 드로우 정보
     * ========================= */

    private String drawNm;

    private Integer totalQty;
    private Integer currentQty;
    private Integer limitQty;
    private Integer salesPrice;

    /* =========================
     * 일정 정보
     * ========================= */

    private LocalDateTime entryStartDate;
    private LocalDateTime entryEndDate;
    private LocalDateTime drawDate;
    private LocalDateTime pickupStartDate;
    private LocalDateTime pickupEndDate;

    /* =========================
     * 상태
     * ========================= */

    private DrawStatus status;
    
    private String description;
}
