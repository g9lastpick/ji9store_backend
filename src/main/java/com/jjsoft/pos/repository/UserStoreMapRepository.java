package com.jjsoft.pos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jjsoft.pos.entity.UserStoreMapEntity;
import com.jjsoft.pos.entity.UserStoreMapId;

@Repository
public interface UserStoreMapRepository extends JpaRepository<UserStoreMapEntity, UserStoreMapId> {

    /** 해당 유저가 접근 가능한 점포 매핑 (사용중인 것만) */
    List<UserStoreMapEntity> findByUserIdAndUseYn(Long userId, String useYn);

    List<UserStoreMapEntity> findByUserId(Long userId);
}
