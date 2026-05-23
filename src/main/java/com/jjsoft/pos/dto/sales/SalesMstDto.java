package com.jjsoft.pos.dto.sales;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalesMstDto {
	private Long    storeId;
	private Long    locationId;
	private String  userId;
	private String  locationNm;
	private Long    salesId;
	private String  status; // 현재 상태 - new , org
	private String  salesDate;
	private Integer totalQty;
	private Integer totalPrice;
	private Integer finalPrice;
    private String  paymentType;  // Enum: 'CASH','CARD','POINT', 'GRP'
    private String  salesType;    // Enum: 'VISIT','DELIVERY','PACKING','RESERVATION' , 복합
    private String  salesStatus;    // Enum: 'COMPLETE','CANCEL','RETURN','RESERVATION'
    private String  description;
    private String  createUser;
    private Double  rate;//할인율
    
    private List<SalesDtlDto> dtlList;
    
    
    private String updateUser;
    private String updateDate;
    
}