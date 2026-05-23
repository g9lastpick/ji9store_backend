package com.jjsoft.pos.dto.sales.condition;

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
public class SalesSearchCondition {
	private Long    storeId;
	private Long    locationId;
	
	private String  userId; 
	private String  salesStatus; // 결제 상태
	private String  paymentType; // 결제 타입
	private String  salesType;   // 구매 타입 - 방문 , 배달 , 예약 등
    private String  startDate;
    private String  endDate;

}
