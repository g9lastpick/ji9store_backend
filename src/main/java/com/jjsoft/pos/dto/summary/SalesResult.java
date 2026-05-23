package com.jjsoft.pos.dto.summary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SalesResult {

    private int saleQty;
    private int salesPrice;
    private int discountPrice;
    
    /** 1 : 알수없는 에러
     *  2 : 실재 판매 수량 없음 , 엑셀 판매수량 에러
     *  3 : 상품정보 없음 바코드 에러
     *  4 : 재고없음 해당 지점에 해당 상품 아이디로 재고 없음
     *  5 : 재고 부족 엑셀 판매수량보다 재고가 부족함.
     * */
    private int errorCode;// 가용수량 에러 : 1
    private int abailableQty;//총 가용 수량

}
