package com.jjsoft.pos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.StoreNotifyConfigEntity;

@Repository
public interface StoreNotifyConfigRepository extends JpaRepository<StoreNotifyConfigEntity, Long> {

    Optional<StoreNotifyConfigEntity> findByStoreIdAndUseYn(Long storeId, String useYn);
}
