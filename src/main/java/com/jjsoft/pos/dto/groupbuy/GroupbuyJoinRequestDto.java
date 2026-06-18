package com.jjsoft.pos.dto.groupbuy;

import java.time.LocalDateTime;
import java.util.List;

import com.jjsoft.pos.enums.GroupbuyJoinStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 공동구매 참여 요청 DTO */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupbuyJoinRequestDto {

    private Long groupbuyId;

    private String userId;

    /** 참여 수량 (0이면 취소로 판단 가능) */
    private int joinQty;

    /** JOIN / CANCEL */
    private GroupbuyJoinStatus requestStatus;

    /** true면 기존 예약수량에 joinQty 누적(모바일 카드 '예약하기'), false면 절대 총량 세팅(마이페이지/어드민) */
    private boolean addQty;
}
