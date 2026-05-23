package com.jjsoft.pos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.ProductDtlEntity;

@Repository
public interface ProductDtlRepository extends JpaRepository<ProductDtlEntity, Long> {// 
	
	// 상품 ID와 lotNo가 중복되는지 확인하는 메서드
	@Query("""
		    SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
		    FROM ProductDtlEntity p
		    WHERE p.product.productId = :productId AND p.lotNo = :lotNo
		""")
	boolean existsByProductIdAndLotNo(@Param("productId") Long productId, @Param("lotNo") String lotNo); 
	
}
