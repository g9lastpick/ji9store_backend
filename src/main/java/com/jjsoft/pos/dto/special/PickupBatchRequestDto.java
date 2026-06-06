package com.jjsoft.pos.dto.special;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 통합 픽업 일괄 완료 요청
 *  - specialRsvMstIds : 특가 예약 마스터 ID 목록
 *  - groupbuyJoinIds  : 공동구매 참여 마스터 ID 목록
 *  (픽업 가능한 항목만 전달)
 */
@Data
public class PickupBatchRequestDto {
    private List<Long> specialRsvMstIds = new ArrayList<>();
    private List<Long> groupbuyJoinIds  = new ArrayList<>();
}
