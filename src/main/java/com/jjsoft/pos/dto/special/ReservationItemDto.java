package com.jjsoft.pos.dto.special;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 특가상품 예약 마스터 / 상세 조회 및 저장용 dto */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationItemDto {


    // 예약 마스터
    private String userId;
    private Long   specialRsvMstId;
    private Long   specialId;
    private Long   salesId;
    private Long   locationId;

    
 // ✅ 동적 상품 컬럼들 (ex. PRODUCT_119 → 2)
    private Map<String, Integer> productQtyMap;
    private Map<String, Long>    rsvMstIdMap;       // SPECIAL_RSV_MST_ID
    private Map<String, Long>    rsvDtlIdMap;      // SPECIAL_RSV_DTL_ID
    private Map<String, Long>    specialIdMstMap;   // SPECIAL_ID
    private Map<String, Long>    specialIdDtlMap;   // SPECIAL_DTL_ID
    private Map<String, Long>    specialPriceMap;   // 상품별 특별 가격 매핑
    private Map<String, Long>    totQtyMap;         // 예약가능 총 수량 매핑
    private Map<String, Long>    remainQtyMap;      // 남은 총 수량 매핑
    
    
    private Integer reservationPrice;
    private Integer salesPrice;
    private Integer unitPrice;
    private String  reservationStatus;
    private String  gender;
    private String  age;
    private String  description;
    
    /** 2025 10 31 추가내역  */
    private Integer totQty;//예약가능 총수량
    private Integer remainQty;//예약가능 남은수량
    
    private boolean _isNew;
    
    private String name;
    private String phone;
    
    
//    private Integer mstReservationCnt;
//    private Integer mstSalesCnt;
//    private Integer mstReservationPrice;
//    private Integer mstSalesPrice;
//    private String  mstVisitDate;
//    private String  mstCancelYn;
//    private String  mstVisitYn;
//    
//
//    // 예약 상세
//    private Long    dtlSpecialRsvDtlId;
//    private Long    dtlSpecialDtlId;
//    private Integer dtlReservationCnt;
//    private Integer dtlSalesCnt;
//    
//
//    // 표시용
//    private String specialNm;
//    private String productNm;
//    private String displayName;
    
    private String startDate;
    private String endDate;
}
