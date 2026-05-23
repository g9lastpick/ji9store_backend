package com.jjsoft.pos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.StorePartnerMstEntity;
@Repository
public interface StorePartnerMstRepository extends JpaRepository<StorePartnerMstEntity, Long>{

}
