package com.jjsoft.pos.dto.mobile;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 「지구 한바퀴」 - 마이페이지 지역 스탬프 응답.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegionStampResponseDto {

    /** 7권역 스탬프 상태(표시 순서 고정) */
    private List<RegionStampDto> stamps;

    /** 획득한 권역 수 */
    private int completedCount;

    /** 전체 권역 수 (=7) */
    private int totalRegions;

    /** 전권역 완주 여부 */
    private boolean allCompleted;
}
