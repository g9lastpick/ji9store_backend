package com.jjsoft.pos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jjsoft.pos.entity.SalesReceiptEntity;
import com.jjsoft.pos.enums.ReceiptType;

/**
 * 매출 영수증 Repository
 */
public interface SalesReceiptRepository extends JpaRepository<SalesReceiptEntity, Long> {

    /**
     * 매출 기준 영수증 조회
     */
    List<SalesReceiptEntity> findBySalesId(Long salesId);

    /**
     * 매출 + 영수증 타입 조회
     */
    List<SalesReceiptEntity> findBySalesIdAndReceiptType(
            Long salesId,
            ReceiptType receiptType
    );

    /**
     * POS 거래 ID 기준 조회
     */
    Optional<SalesReceiptEntity> findByPosTxId(String posTxId);
}
