package com.jjsoft.pos.dto.special;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 특가 상세 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialDtlDto {
	
	// JPQL DTO 매핑용 생성자
    public SpecialDtlDto(String productNm, Integer remainQty) {
        this.productNm = productNm;
        this.remainQty = remainQty;
    }

    private Long specialDtlId;      // 특가 상세 ID
    private Long specialId;         // 특가 마스터 ID
    private String specialNm;         // 특가 마스터 ID
    private Long productId;         // 상품 ID
    private String productNm;         // 상품 ID
    private String categoryNm;         // 상품 ID
    private String partnerNm;         // 상품 ID
    private String progressType;         // 진행타입 start , stop , cancel
                   
    private Integer qty;            // 판매 상품 수량
    private Integer remainQty;      // 판매 남은 수량
    private Integer limitQty;    //2025 09 01 추가// 1인당 구매 가능한 수량
    
    private Integer orgSalesPrice;  // 원래 판매가 (권장 소비자가)
    private Integer salesPrice;     // 특가가
    private Double salesRate;       // 할인율 (예: 20.0%)

    private String description;     // 비고
    private String createUser;      // 생성자
    private String updateUser;      // 수정자
    
    private String startDate;      // 특가 시작일시
    private String endDate;      // 특가 종료일시

    private String createDate;  // 생성일자
    private String updateDate;  // 수정일자

 
    
    //모바일 정보 추가
    private Integer orgPrice;//권장 소비자가
    private Integer costPrice;//입고시 매입가
    private Integer remainStock;// 남은 재고수량  - 특가입력 수량에서 예약수량과 판매수량 빼고 나옴
    private Double discountRate;//할인율(%)
    private Integer specialPrice;//특가 판매가
    private String imageUrl;
    private String expirationDate;//소비기한ㅁa
    private Integer sortOrder;
    
    //기존 예약 정보
    private Integer myReservationQty ;  
    private Integer mySalesQty     ;    
    private Integer myReservationPrice ;
    private Integer mySpecialRsvMstId; // 기존 예약 ID
    private String  myReservationStatus;// -- 예약 상태
    
    
    private String rowType;
    private String mainImageUrl;
    private String locationNm;
    private String address;
    
    private String pickupEndDate; // 픽업 완료 식간
    
    
  
}
