package com.jjsoft.pos.repository;

import com.jjsoft.pos.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 특가 마스터 Repository
 */
@Repository
public interface SpecialMstRepository extends JpaRepository<SpecialMstEntity, Long> {
}
