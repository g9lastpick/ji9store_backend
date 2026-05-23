package com.jjsoft.pos.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalesDtlDto {
	private Long salesDtlId;             // 상품 디테일 ID
	private Long salesId;             // 상품 디테일 ID
	private Long productDtlId;             // 상품 디테일 ID
	private Long productId;                // 상품 ID
	private Long storeId;                  // 매장 ID
	private Long categoryId;               // 카테고리 ID
	private Long partnerId;                // 파트너 ID
    private Long locationId;               // 매장 위치 ID
    private String storeNm;                // 스토어
    private String status;              // 상품 상태 (ACTIVE, INACTIVE, DELETED)
    private String partnerNm;              // 파트너명
    private String locationNm;             // 매장 위치명
    private String categoryNm;             // 카테고리명
    private String productNm;              // 상품명
    private String paymentType;              // 상품명
    private String salesType;              // 상품명
    private String salesStatus;              // 상품명
    private String lotNo;                  // 상품 랏 번호
    private Integer qty;                   // 수량
    private Integer unitPrice;             // 매입가
    private Integer costPrice;             // 매입가
    private Integer orgPrice;              // 권장소비자가
    
    private Integer orgSalesPrice;         // 현장가 
    private Integer salesPrice;            // 현장가 - qty * orgSalesPrice = 실판매가
    private Integer inputSalesPrice;       // 입력한 판매가 salesPrice/qty
    private Integer discountPrice;       // 할인금액
    
    private Integer agreedPrice;           // 협의가
    private String description;            // 상품 설명
    private String  editor;                // 수정가능여부
    private boolean _isNew;//신규 여부
    
    
    private String updateUser;
    private String updateDate;
}
