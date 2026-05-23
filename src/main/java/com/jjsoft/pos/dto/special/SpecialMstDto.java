package com.jjsoft.pos.dto.special;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 특가 마스터 DTO (상세 포함)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialMstDto {

    private Long specialId;         // 특가 ID
    private Long storeId;           // 매장 ID
    private Long locationId;        // 위치 ID

    private String specialNm;       // 특가 명

    private LocalDateTime reserveDate; // 특가 일자
    private LocalDateTime reserveTime; // 특가 시간

    private LocalDateTime startDate;   // 특가 시작일
    private LocalDateTime endDate;     // 특가 종료일

    private Integer specialPrice;      // 특가 금액
    private String specialType;        // 타입 (EVENT / ETC)
    private String progressType;       // 진행상태 (READY / START / STOP / CANCEL)

    private String description;        // 비고
    private String createUser;         // 생성자
    private String updateUser;         // 수정자

    private LocalDateTime createDate;  // 생성일자
    private LocalDateTime updateDate;  // 수정일자
    
    private LocalDateTime pickupEndDate; // 픽업 일시
    
   

    private List<SpecialDtlDto> dtlList; // ✅ 특가 상품 상세 리스트
}
