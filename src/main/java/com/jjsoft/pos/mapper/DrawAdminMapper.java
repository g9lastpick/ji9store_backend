package com.jjsoft.pos.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.jjsoft.pos.dto.draw.DrawDetailResponseDto;
import com.jjsoft.pos.dto.draw.DrawResponseDto;
import com.jjsoft.pos.dto.draw.DrawSearchRequestDto;

@Mapper
public interface DrawAdminMapper {

	
	/** 드로우 월별 리스트 조회 (관리자) */
	public List<DrawResponseDto> selectDrawList(DrawSearchRequestDto requestDto);
    
    /** 드로우 단건 상세 */
    public DrawDetailResponseDto selectDrawDetail(@Param("drawId") Long drawId);
	
    /** 티켓 번호 조회 쿼리 */
    public int selectMaxTicketNoForUpdate(@Param("drawId") Long drawId);
    
    /** 랜덤 티켓 추첨 */
    List<Integer> selectRandomWinnerTickets( @Param("drawId") Long drawId, @Param("winnerCnt") int winnerCnt );
    
    /** 당첨 조회 */
    List<Long> selectWinnerEntryIdsByTicketNos( @Param("drawId") Long drawId, @Param("ticketNos") List<Integer> ticketNos );
    
    /**  당첨자 상태 업데이트 (ENTRY → WIN) */
    int updateEntryStatus(@Param("drawId") Long drawId , @Param("entryIds") List<Long> entryIds );

    /** 탈락자 상태 업데이트 (남은 ENTRY → LOSE) */
    int updateLoseEntries(@Param("drawId") Long drawId);

    
    
    
    
    /** batch : 추첨대상 드로우 조회 */
    List<Long> selectDrawIdsToDraw();
    /** batch : 드로우 마스터 업데이트 DRAW */
    int updateDrawStatusToDraw(@Param("drawId") Long drawId);
    
    
    /** batch : 픽업 종료 대상 드로우 조회 */
    List<Long> selectDrawIdsForPickupEnd();

    /** batch : 노쇼 처리 */
    int updateNoShowWinners(@Param("drawId") Long drawId);

    /** batch : 드로우 종료 처리 */
    int updateDrawStatusToEnd(@Param("drawId") Long drawId);
	
	
}
