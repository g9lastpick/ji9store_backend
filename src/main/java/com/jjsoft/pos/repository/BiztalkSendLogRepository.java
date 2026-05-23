package com.jjsoft.pos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.BiztalkSendLogEntity;




@Repository
public interface BiztalkSendLogRepository  extends JpaRepository<BiztalkSendLogEntity, Long> {

	Optional<BiztalkSendLogEntity> findByMsgIdx(String msgIdx);
}
