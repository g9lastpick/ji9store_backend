package com.jjsoft.pos.dto.draw;

import com.jjsoft.pos.enums.DrawEntryStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/** 드로우 참여용 dto */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DrawEntryRequestDto {

    /** 드로우 ID */
    private Long drawId;

    /** 유저 ID */
    private String userId;

    /** 요청 상태 (ENTRY / CANCEL) */
    private DrawEntryStatus entryStatus;
}