package com.jjsoft.pos.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.DailySalesItemEntity;

@Repository
public interface DailySalesItemRepository extends JpaRepository<DailySalesItemEntity, Long>{

	
	 Optional<DailySalesItemEntity> findBySalesDateAndBarcode(LocalDate salesDate, String barcode);
	 
	 void deleteBySalesDate(LocalDate salesDate);
}
