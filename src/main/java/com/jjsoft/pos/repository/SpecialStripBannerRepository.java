package com.jjsoft.pos.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jjsoft.pos.entity.SpecialStripBannerEntity;

public interface SpecialStripBannerRepository extends JpaRepository<SpecialStripBannerEntity, Long> {

    /** 어드민 목록: 기본배너 → 정렬순 → 최신 */
    List<SpecialStripBannerEntity> findByStoreIdOrderByIsDefaultDescSortOrderAscCreatedAtDesc(Long storeId);

    /** 오늘 기간에 드는 활성 일반배너(캐러셀 후보) */
    @Query("""
            SELECT b FROM SpecialStripBannerEntity b
             WHERE b.storeId = :storeId
               AND b.isActive = true
               AND b.isDefault = false
               AND b.startDate <= :today
               AND b.endDate >= :today
             ORDER BY b.sortOrder ASC, b.createdAt DESC
            """)
    List<SpecialStripBannerEntity> findActiveBanners(@Param("storeId") Long storeId,
                                                     @Param("today") LocalDate today);

    /** 기간 외 노출용 기본배너 */
    List<SpecialStripBannerEntity> findByStoreIdAndIsActiveTrueAndIsDefaultTrueOrderByCreatedAtDesc(Long storeId);
}
