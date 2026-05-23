package com.jjsoft.pos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.dto.product.ProductImageDto;
import com.jjsoft.pos.entity.ProductImageEntity;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {
	

	@Query("SELECT new com.jjsoft.pos.dto.product.ProductImageDto(p.id, p.imageUrl) " +
           "FROM ProductImageEntity p " +
           "WHERE p.productId = :productId AND p.useYn = 'Y' " +
           "ORDER BY p.sortOrder ASC")
    List<ProductImageDto> findImagesByProductId(@Param("productId") Long productId);
	
	
	@Query("SELECT MAX(p.sortOrder) FROM ProductImageEntity p WHERE p.productId = :productId")
	Integer findMaxSortOrderByProductId(@Param("productId") Long productId);
}
