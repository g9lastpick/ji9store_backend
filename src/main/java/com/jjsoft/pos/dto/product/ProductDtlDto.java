package com.jjsoft.pos.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDtlDto {
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
    private String lotNo;                  // 상품 랏 번호
    private String pProductCd;             // 상품 코드
    private String pVarcodeNo;             // 바코드 번호
    private String receivedDate;           // 입고 일
    private Integer boxQty;                // BOX 수량
    private Integer orgStockQty;           // 입고일 기준 입고된 재고 수량
    private Integer curStockQty;           // 남은 재고 수량
    private String expirationDate;         // 유통기한
    private Integer costPrice;             // 매입가
    private Integer orgPrice;              // 권장소비자가
    private Integer orgSalesPrice;             // 현장가 
    private Integer salesPrice;             // 현장가 - 유통기한에 따른 남은일수 계산된 가격
    private Integer agreedPrice;           // 협의가
    private Integer etcPrice;              // 협의가
    private String description;            // 상품 설명
    private String  editor;                // 수정가능여부
    private boolean _isNew;//신규 여부
    
    
    private String updateUser;
    private String updateDate;
}
