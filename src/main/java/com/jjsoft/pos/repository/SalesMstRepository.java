package com.jjsoft.pos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.SalesMstEntity;

@Repository
public interface SalesMstRepository extends JpaRepository<SalesMstEntity, Long> {

    @Query(value = "SELECT PRODUCT_ID, COUNT(*) FROM sales_dtl WHERE PRODUCT_ID IN (:productIds) GROUP BY PRODUCT_ID", nativeQuery = true)
    List<Object[]> countSalesByProductIds(@Param("productIds") List<Long> productIds);
}
