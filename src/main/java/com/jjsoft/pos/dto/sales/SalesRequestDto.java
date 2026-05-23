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
public class SalesRequestDto {

	private Long   salesId;
	private String userId;
	private String status; // 현재 상태 - new , org
    private Long   storeId;
    private String paymentType;  // Enum: 'CASH','CARD','POINT', 'GRP'
    private String salesType;    // Enum: 'VISIT','DELIVERY','PACKING','RESERVATION' , 복합
    private String salesStatus;    // Enum: 'COMPLETE','CANCEL','RETURN','RESERVATION'
    private String description;
    private String createUser;

    private List<SalesDetailDto> details; 

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SalesDetailDto {
    	private String status; // 현재 상태 - new , org
        private Long salesDtlId;
        private Long salesId;
        private Long productId;
        private Long productDtlId;
        private Long salesType;
        private Long paymentType;
//        private Long lineNo;
        private Integer qty;
        private Integer unitPrice;
        private Integer discountPrice;
        private Integer discountRate;
        private Integer salesPrice;
        private String description;
    }
}
