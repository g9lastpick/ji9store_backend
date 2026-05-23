package com.jjsoft.pos.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.jjsoft.pos.dto.mobile.ReferralResponseDto;

@Mapper
public interface MobileReferralMapper {

	public ReferralResponseDto getReferralInfo(@Param("userId") String userId);
}
