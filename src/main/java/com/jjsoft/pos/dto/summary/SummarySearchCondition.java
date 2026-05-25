package com.jjsoft.pos.dto.summary;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	// JWT의 store_id claim에서 controller가 강제 주입. 클라이언트가 보낸 값은 무시된다.
	@JsonIgnore
	private Long    storeId;
	private Long    locationId;

    private String  startDate;
    private String  endDate;

    private String type;

}






