package com.jjsoft.pos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.BatchLogEntity;

@Repository
public interface BatchLogRepository extends JpaRepository<BatchLogEntity, Long>{

}
