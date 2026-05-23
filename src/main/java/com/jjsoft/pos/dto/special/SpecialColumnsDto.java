package com.jjsoft.pos.dto.special;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialColumnsDto {

	private String specialId;
	private String specialDtlId;
	private String specialNm;
	private String productNm;
	private String salesPrice;
	private String totQty;
	private String remainQty;
}
