package com.jjsoft.pos.dto.summary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SummarySearchCondition {
	private Long    storeId;
	private Long    locationId;

    private String  startDate;
    private String  endDate;
    
    private String type;

}






