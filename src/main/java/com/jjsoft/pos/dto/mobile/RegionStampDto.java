package com.jjsoft.pos.dto.mobile;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 「지구 한바퀴」 - 권역 1개의 스탬프 상태.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegionStampDto {

    /** 권역명 (서울/경기도/강원도/충청도/경상도/전라도/제주도) */
    private String region;

    /** 스탬프 획득 여부 (해당 권역 구매 1건 이상) */
    private boolean stamped;

    /** 해당 권역 누적 구매 건수 */
    private long count;

    /** 최초 스탬프 획득(=최초 구매) 일시. 미획득이면 null */
    private LocalDateTime firstStampedDate;
}
