package com.jjsoft.pos.dto.special.condition;

import java.util.List;

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
public class SpecialSearchCondition {

	private Long    storeId;
	private Long    locationId;
	private Long    specialId;
	private Long    categoryId;

	private String  specialNm; 
	private String  reservationStatus; // 결제 상태
	private String  startDate;
	private String  endDate;
	
	
	private List<String> colList;
	
	
	private String userId;
	private String productId;
	
	private String type;
	
	
}
