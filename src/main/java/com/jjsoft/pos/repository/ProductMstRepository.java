package com.jjsoft.pos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.ProductMstEntity;

@Repository
public interface ProductMstRepository extends JpaRepository<ProductMstEntity, Long>{
	
	/* 신규저장 중복체크 */
	@Query("""
		    SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
		    FROM ProductMstEntity p
		    WHERE p.store.id = :storeId
		      AND p.partner.id = :partnerId
		      AND p.category.id = :categoryId
		      AND p.productNm = :productNm
		""")
		boolean existsProduct(
		    @Param("storeId") Long storeId,
		    @Param("partnerId") Long partnerId,
		    @Param("categoryId") Long categoryId,
		    @Param("productNm") String productNm
		);
	
	/* 기존 데이터 중복 체크 */
	@Query("""
		    SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
		      FROM ProductMstEntity p
		     WHERE p.store.id     = :storeId
		       AND p.partner.id   = :partnerId
		       AND p.category.id  = :categoryId
		       AND p.productNm    = :productNm
		       AND p.id           != :productId
		""")
		boolean existsProductExceptSelf(@Param("storeId") Long storeId,
		                                @Param("partnerId") Long partnerId,
		                                @Param("categoryId") Long categoryId,
		                                @Param("productNm") String productNm,
		                                @Param("productId") Long productId);

	
	Optional<ProductMstEntity> findByPvarcodeNo(String barcode);
	
	/**
     * 바코드 존재 여부 체크
     */
    @Query("SELECT p.pvarcodeNo FROM ProductMstEntity p WHERE p.pvarcodeNo IN :varcodes")
    List<String> findExistingVarcodes(@Param("varcodes") List<String> varcodes);
}
