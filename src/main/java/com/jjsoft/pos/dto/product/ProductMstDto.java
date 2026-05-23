package com.jjsoft.pos.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductMstDto {
	private Long productId;
    private Long storeId;
    private Long categoryId;
    private Long partnerId;
	
	private String status;                 // 파트너명
	private String partnerNm;              // 파트너명
    private String categoryNm;             // 카테고리명
    private String locationNm;             // 점포명
    private String productNm;              // 상품명
    private String pproductCd;              // 계열사 상품코드
    private String pvarcodeNo;              // 계열사 상품 바코드
    private String description;            // 상품 설명
    private String productStatus;          // 상품 상태 (ACTIVE, INACTIVE, DELETED)
    private String firstReceivedDate;      // 입고 시작 일
    private String  lastReceivedDate;      // 입고 종료 일
    private Integer orgPrice;              // 소비자가
    private Integer orgSalesPrice;         // 현장가
    private Integer boxQty;                // BOX 수량 - 총입고된 박스 수량
    private Integer orgStockQty;           // 총 입고된 입고 수량
    private Integer curStockQty;           // 총 남은 재고 수량
    private String  editor;                // 수정가능여부
    
    
    private String createUser;
    private String  receivedDate;                // 입고일
    private String expirationDate; // 소비기한 / 유통기한
    private boolean _isNew;
    
    //모바일용 메인 이미지
    private String imageUrl;
    private Integer discountRate;
    
    private String updateUser;
    private String updateDate;
    
}