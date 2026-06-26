package com.jjsoft.pos.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.jjsoft.pos.dto.mobile.PartnerAddressAggDto;

@Mapper
public interface MobileMyPageMapper {

    /**
     * 「지구 한바퀴」 - 사용자의 완료(COMPLETE) 구매를 계열사 주소 단위로 집계.
     * 공백 주소 계열사는 제외한다.
     *
     * @param userId  로그인 사용자 식별자(=email, sales_mst.USER_ID)
     * @param storeId 점포 ID(점포 격리)
     */
    List<PartnerAddressAggDto> selectPartnerAddressAgg(@Param("userId") String userId,
                                                       @Param("storeId") Long storeId);
}
