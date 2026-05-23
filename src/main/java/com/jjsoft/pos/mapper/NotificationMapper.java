package com.jjsoft.pos.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.jjsoft.pos.dto.biztalk.BiztalkRequestDto;
import com.jjsoft.pos.dto.biztalk.BiztalkSendLogDto;
import com.jjsoft.pos.dto.biztalk.TemplateMstDto;

@Mapper
public interface NotificationMapper {
	
	/** 알림톡 전송 리스트 */
    List<Map<String, Object>> findTodayPickupReservationUsers();
    List<Map<String, Object>> findTodayPickupReservationUsers1();
    
    Map<String, Object> getBiztalkTmpl(String tmplCode);
	/**
     * 템플릿 리스트 조회
     * 조건: 템플릿명 / 템플릿코드
     */
    List<TemplateMstDto> selectTemplateList(BiztalkRequestDto dto);
    
    /** 알림톡 전송 로그 리스트 조회 */
    List<BiztalkSendLogDto> selectAlimtalkLogList(BiztalkRequestDto dto);
}
