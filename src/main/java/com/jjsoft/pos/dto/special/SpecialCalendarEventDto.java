package com.jjsoft.pos.dto.special;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialCalendarEventDto {
    private Long id;
    private String title;
    private String specialNm;
    private String progressType;
    private String start;
    private String end;
    private String backgroundColor;
}