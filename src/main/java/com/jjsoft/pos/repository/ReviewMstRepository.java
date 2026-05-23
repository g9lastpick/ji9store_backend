package com.jjsoft.pos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.ReviewMstEntity;

@Repository
public interface ReviewMstRepository extends JpaRepository<ReviewMstEntity, Long> {

    Page<ReviewMstEntity> findByProductIdAndStatus(Long productId, String status, Pageable pageable);

    Optional<ReviewMstEntity> findByProductIdAndUserId(Long productId, String userId);

    long countByProductIdAndStatus(Long productId, String status);

    @Query("SELECT AVG(r.rating) FROM ReviewMstEntity r WHERE r.productId = :productId AND r.status = 'ACTIVE'")
    Double averageRatingByProductId(@Param("productId") Long productId);

    @Query("SELECT r.rating AS rating, COUNT(r) AS cnt " +
           "FROM ReviewMstEntity r " +
           "WHERE r.productId = :productId AND r.status = 'ACTIVE' " +
           "GROUP BY r.rating " +
           "ORDER BY r.rating DESC")
    List<Object[]> ratingDistribution(@Param("productId") Long productId);
}
