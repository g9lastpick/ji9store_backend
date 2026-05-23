package com.jjsoft.pos.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.jjsoft.pos.dto.product.ProductMstDto;
import com.jjsoft.pos.dto.special.SpecialDtlDto;
import com.jjsoft.pos.dto.special.SpecialMstDto;
import com.jjsoft.pos.dto.special.condition.SpecialSearchCondition;

@Mapper
public interface MobileSpecialMapper {

	/** 특가 네임 조회 */
	public List<SpecialMstDto>  selectMobileSpecialList(SpecialSearchCondition condition);
	/** 특가 상품 조회 */
	public List<SpecialDtlDto>  selectMobileSpecialDtlList(SpecialSearchCondition condition);
	
	/** 특가 예약전 남은 수량 조회 */
	public List<SpecialDtlDto>  selectMobileSpecialRemainQtyList(SpecialSearchCondition condition);
	/** 특가 상품 상세 보기 조회 */
	public List<SpecialDtlDto>  productDetailImageList(SpecialSearchCondition condition);

	/** 모바이 상품 조회 - 라스트픽 , 신규상품 , 매장 제고 */
	public List<ProductMstDto>  selectMobileProductList(SpecialSearchCondition condition);
	
	
	/** 유저 예약 저장시 수량 체크 및 남은 수량 UPDATE 로직
	 * 해당 함수가 0을 리턴하면 예약수량 초과임... */
	int updateReservationQty(Map<String, Object> paramMap);
	/** 예약 수량 남아있는지 확인 가는 쿼리  */
	boolean isUpdate(Map<String, Object> paramMap);
	
	
	/** 모바일 주문내역 조회 2025 10 29 추가내역 */
	public List<SpecialDtlDto>  selectMobileOrderList(SpecialSearchCondition condition);
}
