package com.jjsoft.pos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.StoreLocationMstEntity;

@Repository
public interface StoreLocationMstRepository extends JpaRepository<StoreLocationMstEntity, Long>{

}
