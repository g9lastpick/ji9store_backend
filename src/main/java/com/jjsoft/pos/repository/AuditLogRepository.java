package com.jjsoft.pos.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.AuditLogEntity;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {
	
	boolean existsByUserIdAndActionTypeAndCreateDateBetween(
            String userId,
            String actionType,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}