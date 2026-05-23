package com.jjsoft.pos.dto.summary;

import lombok.*;
import java.time.LocalDate;

/**
 * 엑셀 업로드용 일별 상품 매출 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DailySalesItemExcelDto {

    /* =========================
     * 기본 정보
     * ========================= */

	private String  salesDate;        // A열: 일자 (예: 20250715)

    private Long    locationId;       // B열: 지점 ID
    private String    locationNm;    
    private String    salesType;    

    private String  categoryNm;       // C열: 카테고리명
    private String  productNm;        // D열: 상품명
    private String  barcode;          // E열: 88코드

    private Integer unitPrice;        // F열: 상품 단가
    private Integer qty;              // G열: 수량

    private Integer totalAmount;      // H열: 총 매출액
    private Integer realAmount;       // I열: 실 매출액
    private Integer discountAmount;   // J열: 할인금액

    private Integer refundAmount;     // K열: 환불금액
    private Integer refundQty;        // L열: 환불 수량

}