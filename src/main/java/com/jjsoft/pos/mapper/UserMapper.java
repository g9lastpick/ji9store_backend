package com.jjsoft.pos.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.jjsoft.pos.dto.user.UserDto;
import com.jjsoft.pos.dto.user.UserSearchDto;

@Mapper
public interface UserMapper {

	List<UserDto> selectPickupList(UserSearchDto dto);
}
