package com.jjsoft.pos.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.jjsoft.pos.dto.mobile.ProductRegionAggDto;

@Mapper
public interface MobileMyPageMapper {

    /**
     * 「지구 한바퀴」 - 사용자의 완료(COMPLETE) 구매를 상품명 단위로 집계.
     * 권역 판정은 상품명 안의 "(지역명)" 텍스트로 서비스에서 수행한다.
     *
     * @param userId  로그인 사용자 식별자(=email, sales_mst.USER_ID)
     * @param storeId 점포 ID(점포 격리)
     */
    List<ProductRegionAggDto> selectProductNameAgg(@Param("userId") String userId,
                                                   @Param("storeId") Long storeId);
}
