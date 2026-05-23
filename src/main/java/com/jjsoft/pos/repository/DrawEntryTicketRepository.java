package com.jjsoft.pos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.DrawEntryTicketEntity;

/**
 * 드로우 응모 티켓 Repository
 */
//@Repository
public interface DrawEntryTicketRepository  {//extends JpaRepository<DrawEntryTicketEntity, Long>

    /**
     * 드로우 기준 전체 티켓 조회
     */
    List<DrawEntryTicketEntity> findByDrawId(Long drawId);

    /**
     * 응모 기준 발급된 티켓 조회
     */
    List<DrawEntryTicketEntity> findByDrawEntryId(Long drawEntryId);

    /**
     * 드로우 + 티켓 번호 단건 조회
     */
    Optional<DrawEntryTicketEntity> findByDrawIdAndTicketNo(
            Long drawId,
            Integer ticketNo
    );
    
    
    
    /**
     * 드로우 참여 취소 시 티켓 전체 삭제
     */
    @Modifying
    @Query("""
        DELETE
          FROM DrawEntryTicketEntity t
         WHERE t.drawEntryId = :drawEntryId
    """)
    int deleteByDrawEntryId(@Param("drawEntryId") Long drawEntryId);
    
    
    
}
