package com.jjsoft.pos.dto.draw;

import java.time.LocalDateTime;

import com.jjsoft.pos.dto.groupbuy.GroupbuyResponseDto;
import com.jjsoft.pos.enums.GroupbuyStatus;
import com.jjsoft.pos.enums.GroupbuyType;
import com.jjsoft.pos.enums.PayType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 드로우 상세 조회 DTO */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DrawDetailResponseDto {

	private Long drawId;

    private Long storeId;
    private Long locationId;
    private Long productId;

    private String drawNm;
    private String drawUrl;

    private LocalDateTime entryStartDate;
    private LocalDateTime entryEndDate;
    private LocalDateTime drawDate;
    private LocalDateTime pickupDate;

    private Integer winnerCnt;

    private Integer totalQty;
    private Integer currentQty;
    private Integer limitQty;
    private Integer salesPrice;

    private String status;
    private String description;

    /* =========================
     * 집계 정보 (관리자용)
     * ========================= */

    private Integer entryCnt;    // 참여자 수
    private Integer ticketCnt;   // 발행된 전체 티켓 수
}
